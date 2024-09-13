package com.bom.shop.security;

import com.bom.shop.user.service.UserSignService;
import com.bom.shop.user.vo.UserAccountVO;
import com.bom.shop.user.vo.UserProfileVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TokenAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private final ObjectMapper objectMapper;
    private final UserSignService userSignService;

    public TokenAuthenticationFailureHandler(ObjectMapper objectMapper, UserSignService userSignService){
        this.objectMapper = objectMapper;
        this.userSignService = userSignService;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        Map<String, String> errorDetail = new HashMap<>();

        if(exception instanceof OAuth2AuthenticationException){
            OAuth2AuthenticationException oAuth2Exception = (OAuth2AuthenticationException) exception;
            if("User not found".equals(oAuth2Exception.getError().getDescription())){
                handleNewUser(request, response);
                return;
            }
        }

        errorDetail.put("error", "Authentication failed");
        errorDetail.put("message", exception.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorDetail));
    }

    private void handleNewUser(HttpServletRequest request, HttpServletResponse response) throws IOException{
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) request.getAttribute("oauth2Token");
        if(token == null){
            throw  new IllegalStateException("OAuth2 token not found in request attributes");
        }

        OAuth2User oauth2User = token.getPrincipal();
        String registrationId = token.getAuthorizedClientRegistrationId();
        String email = extractEmail(oauth2User, registrationId);

        UserAccountVO userAccountVO = new UserAccountVO();
        userAccountVO.setRegistrationId(registrationId);
        userAccountVO.setUserRole("USER");

        UserProfileVO userProfileVO = new UserProfileVO();
        userProfileVO.setEmail(email);

        saveUser(userAccountVO, userProfileVO);

        Map<String, String> userDetail = new HashMap<>();
        userDetail.put("message", "New user registered");
        userDetail.put("email", email);
        userDetail.put("registrationId", registrationId);

        response.setStatus(HttpServletResponse.SC_CREATED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(userDetail));
    }

    private String extractEmail(OAuth2User oauth2User, String registrationId){
        switch (registrationId) {
            case "google":
                return oauth2User.getAttribute("email");
            case "naver":
                Map<String, Object> naverAttributes = oauth2User.getAttribute("response");
                return (String) naverAttributes.get("email");
            case "kakao":
                Map<String, Object> kakaoAttributes = oauth2User.getAttribute("kakao_account");
                return (String) kakaoAttributes.get("email");
            default:
                throw new IllegalArgumentException("Unsupported login type: " + registrationId);

        }
    }

    private void saveUser(UserAccountVO userAccountVO, UserProfileVO userProfileVO){

        userSignService.userSignSso(userAccountVO, userProfileVO);
    }
}
