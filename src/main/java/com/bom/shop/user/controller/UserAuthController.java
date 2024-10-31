package com.bom.shop.user.controller;

import com.bom.shop.security.jwtFacadePattern.JwtService;
import com.bom.shop.user.service.RefreshTokenMangeService;
import com.bom.shop.user.service.UserAuthService;
import com.bom.shop.user.vo.UserAccountVO;
import com.bom.shop.user.vo.UserProfileVO;
import com.bom.shop.utility.AuthenticationUtil;
import com.bom.shop.utility.CookieUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class UserAuthController {

    private final JwtService jwtService;
    private final UserAuthService userAuthService;
    private final CookieUtil cookieUtil;
    private final AuthenticationUtil authenticationUtil;
    private final RefreshTokenMangeService refreshTokenMangeService;

    @Autowired
    public UserAuthController(JwtService jwtService, UserAuthService userAuthService, CookieUtil cookieUtil, AuthenticationUtil authenticationUtil, RefreshTokenMangeService refreshTokenMangeService){
        this.jwtService = jwtService;
        this.userAuthService = userAuthService;
        this.cookieUtil = cookieUtil;
        this.authenticationUtil = authenticationUtil;
        this.refreshTokenMangeService = refreshTokenMangeService;
    }

    // 일반 로그인
    @PostMapping("/login/defaultLogin")
    public ResponseEntity<?> defaultLogin(@RequestBody UserAccountVO loginData
            , HttpServletResponse response) {
        try {

            System.out.println(loginData);

            // 일반 로그인
            UserAccountVO userAccountVO = userAuthService.defaultLoginSelect(loginData);
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
    @PostMapping("/login/socialLogin")
    public ResponseEntity<?> socialLogin(@RequestParam(name = "email", required = false) String email
                                        , @RequestParam(name = "registrationId", required = false) String registrationId
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

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestParam(required = false) String logoutType
                                    , @CookieValue(value = "refresh_token", required = false) String refreshToken
                                    , HttpServletResponse response){

        try {
            cookieUtil.deleteAuthTokenCookie(response);

            if(refreshToken != null){
                String email = jwtService.getEmail(refreshToken, true);
                UserAccountVO userAccountVO = userAuthService.findByEmail(email);

                if(userAccountVO != null){
                    refreshTokenMangeService.deleteByUserId(userAccountVO.getUserId());
                }
            }

            String message = "auto".equals(logoutType)
                    ? "자동 로그아웃 되었습니다. 다시 로그인 해주세요."
                    : "로그아웃 되었습니다.";

            return ResponseEntity.ok().body(Map.of(
                    "status", "success"
                    , "message", message
                    , "logoutType", logoutType != null ? logoutType : "default"
            ));

        } catch (Exception e) {
            log.error("Logout failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "error",
                            "message", "로그아웃 중 오류가 발생했습니다."
                    ));
        }
    }

    // 토큰 체크
    @GetMapping("/checkToken")
    public ResponseEntity<?> checkToken(@CookieValue(value = "access_token", required = false) String accessToken
                                        , @CookieValue(value = "refresh_token", required = false) String refreshToken
                                        , HttpServletResponse response) {
        try {
            // 액세스 토큰 체크
            if (accessToken != null && jwtService.validateToken(accessToken, false)) {
                return ResponseEntity.ok().body(Map.of("message", "Valid access token"));
            }

            // 액세스 토큰이 없거나 유효하지 않은 경우, 리프레시 토큰으로 갱신 요청
            if (refreshToken != null && jwtService.validateToken(refreshToken, true)) {

                String email = jwtService.getEmail(refreshToken, true);
                UserAccountVO userAccountVO = userAuthService.findByEmail(email);

                Authentication authentication = authenticationUtil.createAuthentication(userAccountVO);

                // 새 액세스 토큰 발급
                String newAccessToken = jwtService.generateAccessToken(authentication);

                cookieUtil.addAccessTokenCookie(response, newAccessToken);

                return ResponseEntity.ok()
                        .body(Map.of(
                                "message", "Access token refreshed successfully"
                                , "userId", authenticationUtil.getUserId(authentication)
                        ));
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Please Login again"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Occur Server Error"));
        }

    }
}
