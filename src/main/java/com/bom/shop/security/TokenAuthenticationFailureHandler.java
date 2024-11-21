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
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TokenAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private final ObjectMapper objectMapper;
    private final String frontendUrl;

    public TokenAuthenticationFailureHandler(ObjectMapper objectMapper, String frontendUrl){
        this.objectMapper = objectMapper;
        this.frontendUrl = frontendUrl;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        System.out.println("Authentication Failure Handler Stared");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // oauth2 인증 실패 처리
        if(auth instanceof OAuth2AuthenticationToken){
            try {
                OAuth2User oauth2User = ((OAuth2AuthenticationToken) auth).getPrincipal();
                String email = oauth2User.getAttribute("email");
                String registrationId = oauth2User.getAttribute("registrationId");
                Boolean isNewUser = oauth2User.getAttribute("isNewUser");

                System.out.println("Email: " + email);
                System.out.println("RegistrationId: " + registrationId);
                System.out.println("IsNewUser: " + isNewUser);

                if(isNewUser != null && isNewUser){
                    redirectToSignup(response, email, registrationId);
                    return;
                }
            } catch (Exception e) {
                System.out.println("Error processing OAuth2 failure: " + e.getMessage());
            }

        }

        sendErrorResponse(response, exception);
    }

    private OAuth2User getOAuth2UserFromRequest(HttpServletRequest request){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth instanceof OAuth2AuthenticationToken){
            return ((OAuth2AuthenticationToken) auth).getPrincipal();
        }
        return null;
    }

    private String extractRegistrationId(String requestUri){
        // "/login/oauth2/code/google" 에서 프로바이더 추출
        String[] parts = requestUri.split("/");
        return parts[parts.length - 1];
    }

    private void redirectToSignup(HttpServletResponse response, String email, String registrationId) throws IOException{
        String redirectUrl = UriComponentsBuilder
                .fromUriString(frontendUrl + "/auth/sign1/ssoSignup")
                .queryParam("email", email)
                .queryParam("registrationId", registrationId)
                .queryParam("isNewUser", true)
                .build()
                .encode()
                .toUriString();

        System.out.println("Redirect to: " +redirectUrl);
        response.sendRedirect(redirectUrl);
    }

    private void sendErrorResponse(HttpServletResponse response, AuthenticationException exception) throws IOException{
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Authentication failed");
        errorResponse.put("message", exception.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }


}
