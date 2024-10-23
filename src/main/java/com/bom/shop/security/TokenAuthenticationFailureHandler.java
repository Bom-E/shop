package com.bom.shop.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TokenAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private final ObjectMapper objectMapper;

    public TokenAuthenticationFailureHandler(ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        Map<String, Object> responseBody = new HashMap<>();

        if (exception instanceof OAuth2AuthenticationException) {
            OAuth2AuthenticationException oauth2Exception = (OAuth2AuthenticationException) exception;
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication instanceof OAuth2AuthenticationToken) {
                OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
                OAuth2User oAuth2User = oauth2Token.getPrincipal();
                String registrationId = oauth2Token.getAuthorizedClientRegistrationId();
                String email = extractEmail(oAuth2User, registrationId);

                if ("User not found".equals(exception.getMessage())) {
                    responseBody.put("isNewUser", true);
                    responseBody.put("email", email);
                    responseBody.put("registrationId", registrationId);
                    response.setStatus(HttpServletResponse.SC_OK);
                } else {
                    responseBody.put("error", "OAuth2 Authentication failed");
                    responseBody.put("message", exception.getMessage());
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                }
            } else {
                responseBody.put("error", "OAuth2 Authentication failed");
                responseBody.put("message", "OAuth2 user information not available");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } else if (exception instanceof UsernameNotFoundException) {
            responseBody.put("error", "User not found");
            responseBody.put("message", exception.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            responseBody.put("error", "Authentication failed");
            responseBody.put("message", exception.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
    }

    private String extractEmail(OAuth2User oAuth2User, String registrationId) {
        switch (registrationId) {
            case "google":
                return oAuth2User.getAttribute("email");
            case "naver":
                Map<String, Object> naverAttribute = oAuth2User.getAttribute("response");
                return (String) naverAttribute.get("email");
            case "kakao":
                Map<String, Object> kakaoAttribute = oAuth2User.getAttribute("kakao_account");
                return (String) kakaoAttribute.get("email");
            default:
                throw new IllegalArgumentException("Unsupported registrationId: " + registrationId);
        }
    }
}
