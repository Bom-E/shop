package com.bom.shop.security;

import com.bom.shop.security.jwtFacadePattern.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MixAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    public MixAuthenticationSuccessHandler(JwtService jwtService, ObjectMapper objectMapper){
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
    }


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String token = jwtService.generateToken(authentication);

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("accessToken", token);

        if(authentication instanceof OAuth2AuthenticationToken){
            OAuth2User oAuth2User = ((OAuth2AuthenticationToken) authentication).getPrincipal();
            String email = oAuth2User.getAttribute("email");
            tokenMap.put("email", email);
        }
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(tokenMap));
    }
}
