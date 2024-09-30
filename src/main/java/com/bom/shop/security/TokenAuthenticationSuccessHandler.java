package com.bom.shop.security;

import com.bom.shop.security.jwtFacadePattern.JwtService;
import com.bom.shop.user.service.UserSignService;
import com.bom.shop.user.vo.UserAccountVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class TokenAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;
    private final UserSignService userSignService;

    public TokenAuthenticationSuccessHandler(JwtService jwtService, ObjectMapper objectMapper, UserSignService userSignService){
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
        this.userSignService = userSignService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String accessToken = jwtService.generateToken(authentication);
        String refreshToken = jwtService.generateToken(authentication);

        if(authentication instanceof OAuth2AuthenticationToken){
            OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
            OAuth2User oAuth2User = oauth2Token.getPrincipal();
            String registrationId = oauth2Token.getAuthorizedClientRegistrationId();
            String email = extractEmail(oAuth2User, registrationId);

            Map<String, String> params = new HashMap<>();
            params.put("email", email);
            params.put("registrationId", registrationId);

            UserAccountVO user = userSignService.ssoLoginSelect(params);
            String targetUrl = "";
            if(user == null){
                // 회원가입
                targetUrl = UriComponentsBuilder.fromUriString("/userSign/sign1/domSignup")
                        .queryParam("accessToken", accessToken)
                        .queryParam("refreshToken", refreshToken)
                        .queryParam("email", email)
                        .queryParam("registrationId", registrationId)
                        .queryParam("isNewUser", "true")
                        .build().toUriString();
            } else {
                targetUrl = UriComponentsBuilder.fromUriString("/")
                        .queryParam("accessToken", accessToken)
                        .queryParam("refreshToken", refreshToken)
                        .build().toUriString();
            }
            response.sendRedirect(targetUrl);
        } else {
          // sso 말고 토큰
          Map<String, String> toeknMap = new HashMap<>();
          toeknMap.put("accessToken", accessToken);
          toeknMap.put("refreshToken", refreshToken);
          response.setContentType("application/json");
          response.setCharacterEncoding("UTF-8");
          response.getWriter().write(objectMapper.writeValueAsString(toeknMap));
        }
    }

    private String extractEmail(OAuth2User oAuth2User, String registrationId){
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
