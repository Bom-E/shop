package com.bom.shop.user.controller;

import com.bom.shop.security.jwtFacadePattern.JwtService;
import com.bom.shop.user.service.UserAuthService;
import com.bom.shop.user.vo.UserAccountVO;
import com.bom.shop.user.vo.UserProfileVO;
import com.bom.shop.utility.AuthenticationUtil;
import com.bom.shop.utility.CookieUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class UserAuthController {
    @Resource(name = "jwtService")
    JwtService jwtService;
    @Resource(name = "userAuthService")
    UserAuthService userAuthService;
    @Resource(name = "cookieUtil")
    CookieUtil cookieUtil;
    @Resource(name = "authenticationUtil")
    AuthenticationUtil authenticationUtil;

    // 일반 로그인
    @PostMapping("/login")
    public ResponseEntity<?> defaultLogin(@RequestParam(name = "userId", required = false) String userId
                                            , HttpServletResponse response) {
        try {

            // 일반 로그인
            UserAccountVO userAccountVO = userAuthService.normalLoginSelect(userId);
            // 존재하는 유저
            if (userAccountVO != null) {

                Authentication authentication = authenticationUtil.createAuthentication(userAccountVO);

                String accessToken = jwtService.generateAccessToken(authentication);
                String refreshToken = jwtService.generateRefreshToken(authentication);

                cookieUtil.addAuthTokenCookie(response, accessToken, refreshToken);

                return ResponseEntity.ok().body(Map.of(
                        "status", "success"
                        , "userId", userAccountVO.getUserId()
                        , "userRole", userAccountVO.getUserRole()));

            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                        "status", "error"
                        , "message", "아이디 또는 비밀번호가 일치하지 않습니다."
                ));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error"
                    , "message", "서버 오류가 발생했습니다."
            ));
        }
    }

    // 소셜 로그인
    @PostMapping("/socialLogin")
    public ResponseEntity<?> socialLogin(@RequestParam(name = "email", required = false) String email
                                        , @RequestParam(name = "registrationId", required = false)String registrationId
                                        , HttpServletResponse response) {

        try {

            Map<String, String> params = new HashMap<>();
            params.put("email", email);
            params.put("registrationId", registrationId);

            UserAccountVO userAccountVO = userAuthService.ssoLoginSelect(params);

            // 소셜 로그인
            if (userAccountVO != null) {
                Authentication authentication = authenticationUtil.createAuthentication(userAccountVO);

                String accessToken = jwtService.generateAccessToken(authentication);
                String refreshToken = jwtService.generateRefreshToken(authentication);

                cookieUtil.addAuthTokenCookie(response, accessToken, refreshToken);

                return ResponseEntity.ok().body(Map.of(
                        "status", "success"
                        , "userId", userAccountVO.getUserId()
                        , "userRole", userAccountVO.getUserRole()
                        , "email", userAccountVO.getUserProfileVO().getEmail()
                        , "registrationId", userAccountVO.getRegistrationId()));
            } else {


                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                        "status", "error"
                        , "message", "아이디 또는 비밀번호가 일치하지 않습니다."
                ));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error"
                    , "message", "서버 오류가 발생했습니다."
            ));
        }
    }

    // 토큰 체크
    @GetMapping("/checkToken")
    public ResponseEntity<?> checkToken(@CookieValue("access_token") String accessToken
                                        , @CookieValue("refresh_token") String refreshToken) {

        // 엑세스 토큰 체크
        if(accessToken != null && jwtService.validateToken(accessToken, false)){
            return ResponseEntity.ok().build();
        }

        // 리프레시 토큰 체크
        if(refreshToken == null || !jwtService.validateToken(refreshToken, true)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(Map.of("message", "재로그인이 필요합니다."));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}
