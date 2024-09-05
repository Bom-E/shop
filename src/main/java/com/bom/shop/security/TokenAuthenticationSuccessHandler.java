package com.bom.shop.security;

import com.bom.shop.security.jwtFacadePattern.JwtService;
import com.bom.shop.user.service.UserSignService;
import com.bom.shop.user.vo.UserAccountVO;
import com.bom.shop.user.vo.UserProfileVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
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

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("accessToken", accessToken);
        tokenMap.put("refreshToken", refreshToken);

        if(authentication instanceof OAuth2AuthenticationToken){
            OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
            OAuth2User oAuth2User = oauth2Token.getPrincipal();
            String email = oAuth2User.getAttribute("email");
            String loginType = oauth2Token.getAuthorizedClientRegistrationId();

            tokenMap.put("email", email);
            tokenMap.put("loginType", loginType);

            switch (loginType){
                case "google":

            }
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(tokenMap));

        saveRefreshToken(authentication, refreshToken);
    }

    private void handleGoogleUser(OAuth2User oAuth2User){
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");


    }

    private void handleNaverUser(OAuth2User oAuth2User){
        Map<String, Object> attributes = oAuth2User.getAttribute("response");
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");


    }

    private void handleKakaoUser(OAuth2User oAuth2User){
        Map<String, Object> attributes = oAuth2User.getAttribute("kakao_account");
        String email = (String) attributes.get("email");
        Map<String, Object> profile = (Map<String, Object>) attributes.get("profile");
        String name = (String) attributes.get("nickname");


    }

    private void saveUser(String email, String name, String loginType){
        UserAccountVO user = userSignService.findOneByEmail(email);

        if(user == null){
            UserAccountVO userAccountVO = new UserAccountVO();
            userAccountVO.setLoginType(loginType);

            UserProfileVO userProfileVO = new UserProfileVO();
            userProfileVO.setUserEmail(email);
            userProfileVO.setUserName(name);

            userSignService.joinUser(userAccountVO, userProfileVO);
        }
    }

    private void saveRefreshToken(Authentication authentication, String refreshToken) {
        // 리프레시 토큰 저장 코드
    }
}
