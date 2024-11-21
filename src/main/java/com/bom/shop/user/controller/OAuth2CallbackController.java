package com.bom.shop.user.controller;

import com.bom.shop.security.jwtFacadePattern.JwtService;
import com.bom.shop.user.service.UserAuthService;
import com.bom.shop.user.vo.UserAccountVO;
import com.bom.shop.utility.AuthenticationUtil;
import com.bom.shop.utility.CookieUtil;
import com.bom.shop.utility.OAuth2Config;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth/oauth2")
@CrossOrigin(origins = "http://localhost:3000")
public class OAuth2CallbackController {

    private final JwtService jwtService;
    private final UserAuthService userAuthService;
    private final CookieUtil cookieUtil;
    private final AuthenticationUtil authenticationUtil;
    private final OAuth2Config oauth2Config;

    @Autowired
    public OAuth2CallbackController(JwtService jwtService, UserAuthService userAuthService, CookieUtil cookieUtil, AuthenticationUtil authenticationUtil, OAuth2Config oauth2Config){
        this.jwtService =jwtService;
        this.userAuthService = userAuthService;
        this.cookieUtil =cookieUtil;
        this.authenticationUtil = authenticationUtil;
        this.oauth2Config = oauth2Config;
    }

    @PostMapping("/callback/{provider}")
    public ResponseEntity<?> handleOAuthCallback(@PathVariable String provider, @RequestBody Map<String, String> payload, HttpServletResponse response){

        try {
            String code = payload.get("code");
            String state = payload.get("state");

            // api로 토큰 교환
            String accessToken = exchangeCodeForToken(code, provider);

            // api로 사용자 정보 요청
            Map<String, Object> userInfo = getUserInfo(accessToken);
            String email = (String) userInfo.get("email");

            // db에서 사용자 조회
            Map<String, String> params = new HashMap<>();
            params.put("email", email);
            params.put("registrationId", provider);

            UserAccountVO userAccountVO = userAuthService.ssoLoginSelect(params);
            if(userAccountVO == null){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                        "status", "error"
                        , "message", "User not found"));
            }

            // jwt 토큰 생성
            Authentication authentication = authenticationUtil.createAuthentication(userAccountVO);
            String jwtAccessToken = jwtService.generateAccessToken(authentication);
            String jwtRefreshToken = jwtService.generateRefreshToken(authentication);

            cookieUtil.addAuthTokenCookie(response, jwtAccessToken, jwtRefreshToken);

            return ResponseEntity.ok().body(Map.of(
                    "status", "success"
                    , "userId", userAccountVO.getUserId()
                    , "email", email
                    , "userRole", userAccountVO.getUserRole()
            ));

        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error"
                    , "message", e.getMessage()
            ));
        }
    }

    private String exchangeCodeForToken(String code, String provider){
        RestTemplate restTemplate = new RestTemplate();
        String tokenUrl = "https://oauth2.googleapis.com/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", oauth2Config.getGoogle().getClientId());
        params.add("client_secret", oauth2Config.getGoogle().getClientSecret());
        params.add("redirect_uri", oauth2Config.getGoogle().getRedirectUri());
        params.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);

        return (String) response.getBody().get("access_token");
    }

    private Map<String, Object> getUserInfo(String accessToken){
        RestTemplate restTemplate = new RestTemplate();
        String userInfoUrl = "https://www.googleapis.com/oauth2/v3/userinfo";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                userInfoUrl
                , HttpMethod.GET
                , entity
                , Map.class
        );

        return response.getBody();
    }
}
