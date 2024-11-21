package com.bom.shop.security;

import com.bom.shop.security.jwtFacadePattern.JwtService;
import com.bom.shop.user.service.UserAuthService;
import com.bom.shop.user.vo.UserAccountVO;
import com.bom.shop.utility.AuthenticationUtil;
import com.bom.shop.utility.CookieUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class TokenAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;
    private final UserAuthService userAuthService;
    private final CookieUtil cookieUtil;
    private final AuthenticationUtil authenticationUtil;
    @Value("${frontend.url}")
    private String FRONTEND_URL;

    public TokenAuthenticationSuccessHandler(JwtService jwtService, ObjectMapper objectMapper, UserAuthService userAuthService, CookieUtil cookieUtil, AuthenticationUtil authenticationUtil){
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
        this.userAuthService = userAuthService;
        this.cookieUtil = cookieUtil;
        this.authenticationUtil = authenticationUtil;
    }

    // 흐름 :
    // 1. 로그인 성공 시 onAuthenticationSuccess() 메소드 호출
    // 2. onAuthenticationSuccess() 메소드의 로그인 타입 구분
    // 3. 사용자 정보 조회
    //  - OAuth2 로그인의 경우
    //      String email = extractEmail(oauth2User, registrationId);
    //      UserAccountVO user = userAuthService.ssoLoginSelect(params);
    //  - 일반 로그인의 경우
    //      String userId = authentication.getName();
    //      UserAccountVO user = userAuthService.normalLoginSelect(userId);
    // 4. 새로운 Authentication 생성
    //  - Authentication newAuth = authenticationUtil.createAuthentication(user);
    // 5. JWT 토큰 생성 및 쿠키 설정
    //  - setAuthToken(response, newAuth);
    // 6. 응답 데이터 생성 및 전송
    //  - sendSuccessResponse(response, createTokenLoginResponse(userId));
    // 이 이후 AuthenticationUtil의 헬퍼 메소드들 이용 가능.


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        if (authentication instanceof OAuth2AuthenticationToken) {
            // 오어서2 로그인 성공
            handleOauth2Login(response, (OAuth2AuthenticationToken) authentication);
        } else {
            // 일반 로그인 성공
            handleDefaultLogin(response, authentication);
        }
    }

    private void handleOauth2Login(HttpServletResponse response, OAuth2AuthenticationToken oauth2Token) throws IOException{
        OAuth2User oauth2User = oauth2Token.getPrincipal();
        String registrationId = oauth2Token.getAuthorizedClientRegistrationId();
        String email = extractEmail(oauth2User, registrationId);

        System.out.println("OAuth2 Token: " + oauth2Token);
        System.out.println("OAuth2 User Attributes: " + oauth2User.getAttributes());

        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("registrationId", registrationId);

        UserAccountVO user = userAuthService.ssoLoginSelect(params);
        if(user != null){
            processOAuth2Login(user, email, registrationId, response);
        } else {
            redirectToOAuth2Signup(email, registrationId, response);
        }

    }

    private void handleDefaultLogin(HttpServletResponse response, Authentication authentication) throws IOException{
        UserAccountVO loginData = new UserAccountVO();
        loginData.setUserId(authentication.getName());

        UserAccountVO user = userAuthService.defaultLoginSelect(loginData);

        if(user != null){
            processDefaultLogin(user, user.getUserId(), response);
        } else {
            redirectToSignup(response);
        }
    }

    private void processOAuth2Login(UserAccountVO user, String email, String registrationId, HttpServletResponse response) throws IOException{
        Authentication newAuth = authenticationUtil.createAuthentication(user);
        setAuthToken(response, newAuth);

        Map<String, String> responseData = createOAuth2LoginResponse(email, registrationId, user);

        System.out.println("OAuth2 Response Data: " + responseData);

        String targetUrl = UriComponentsBuilder.fromUriString(FRONTEND_URL + "/oauth/callback/" + registrationId)
                .queryParams(convertMapToMultiValueMap(responseData))
                .build().toUriString();

        System.out.println("Redirect URL: " + targetUrl);

        response.sendRedirect(targetUrl);
    }

    private void processDefaultLogin(UserAccountVO user, String userId, HttpServletResponse response) throws IOException{
        Authentication newAuth = authenticationUtil.createAuthentication(user);
        setAuthToken(response, newAuth);
        sendSuccessResponse(response, createTokenLoginResponse(userId, user));
    }

    private void setAuthToken(HttpServletResponse response, Authentication authentication){
        String accessToken = jwtService.generateAccessToken(authentication);
        String refreshToken = jwtService.generateRefreshToken(authentication);

        cookieUtil.addAuthTokenCookie(response, accessToken, refreshToken);
    }

    private Map<String, String> createOAuth2LoginResponse(String email, String registrationId, UserAccountVO user){
        Map<String, String> responseData = new HashMap<>();

        responseData.put("status", "success");
        responseData.put("message", "Login Successful");
        responseData.put("userId", user.getUserId());
        responseData.put("email", email);
        responseData.put("registrationId", registrationId);
        responseData.put("userRole", user.getUserRole());


        return responseData;
    }

    private Map<String, String> createTokenLoginResponse(String userId, UserAccountVO user){
        Map<String, String> responseData = new HashMap<>();

        responseData.put("status", "success");
        responseData.put("message", "Login successful");
        responseData.put("userId", userId);
        responseData.put("userRole", user.getUserRole());

        return responseData;
    }

    private void sendSuccessResponse(HttpServletResponse response, Map<String, String> responseData) throws IOException{
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(responseData));
    }

    private void redirectToOAuth2Signup(String email, String registrationId, HttpServletResponse response) throws IOException{
        String targetUrl = UriComponentsBuilder.fromUriString(FRONTEND_URL + "/auth/sign1/ssoSignup")
                .queryParam("email", email)
                .queryParam("registrationId", registrationId)
                .queryParam("isNewUser", "true")
                .build().toUriString();
        response.sendRedirect(targetUrl);
    }

    private void redirectToSignup(HttpServletResponse response) throws IOException{
        String targetUrl = UriComponentsBuilder.fromUriString(FRONTEND_URL + "/auth/sign1/defaultSignup")
                .build().toUriString();
        response.sendRedirect(targetUrl);
    }

    private MultiValueMap<String, String> convertMapToMultiValueMap(Map<String, String> map){
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        map.forEach(multiValueMap::add);
        return multiValueMap;
    }

    private String extractEmail(OAuth2User oauth2User, String registrationId){
        switch (registrationId) {
            case "google":
                return oauth2User.getAttribute("email");
            case "naver":
                Map<String, Object> naverAttribute = oauth2User.getAttribute("response");
                return (String) naverAttribute.get("email");
            case "kakao":
                Map<String, Object> kakaoAttribute = oauth2User.getAttribute("kakao_account");
                return (String) kakaoAttribute.get("email");
            default:
                throw new IllegalArgumentException("Unsupported registrationId: " + registrationId);
        }
    }

}
