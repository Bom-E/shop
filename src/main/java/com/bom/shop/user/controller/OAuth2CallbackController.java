package com.bom.shop.user.controller;

import com.bom.shop.security.jwtFacadePattern.JwtService;
import com.bom.shop.user.service.UserAuthService;
import com.bom.shop.utility.AuthenticationUtil;
import com.bom.shop.utility.CookieUtil;
import com.bom.shop.utility.OAuth2Config;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    private final OAuth2Config oAuth2Config;

    @Autowired
    public OAuth2CallbackController(JwtService jwtService, UserAuthService userAuthService, CookieUtil cookieUtil, AuthenticationUtil authenticationUtil, OAuth2Config oAuth2Config){
        this.jwtService =jwtService;
        this.userAuthService = userAuthService;
        this.cookieUtil =cookieUtil;
        this.authenticationUtil = authenticationUtil;
        this.oAuth2Config = oAuth2Config;
    }

    @PostMapping("/callback/{provider}")
    public ResponseEntity<?> handleOAuthCallback(@PathVariable String provider, @RequestBody Map<String, String> payload, HttpServletResponse response){

        try {
            String code = payload.get("code");
            String state = payload.get("state");

            String accessToken = exchangeCodeForToken(code, provider);

            Map<String, Object> userInfo = getUser

            Map<String, String> params = new HashMap<>();
            params.put("registrationId", provider);

        } catch (Exception e){

        }
    }

    private String exchangeCodeForToken(String code, String provider){
        RestTemplate restTemplate = new RestTemplate();
        String tokenUrl = "https://oauth2.googleapis.com/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", googleClient);
        params.add();
        params.add();
        params.add();
    }
}
