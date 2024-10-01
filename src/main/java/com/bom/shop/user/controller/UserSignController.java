package com.bom.shop.user.controller;

import com.bom.shop.security.jwtFacadePattern.JwtService;
import com.bom.shop.user.service.UserSignService;
import com.bom.shop.user.vo.UserAccountVO;
import com.bom.shop.user.vo.UserProfileVO;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/userSign")
@CrossOrigin(origins = "http://localhost:3000")
public class UserSignController {
    JwtService jwtService;
    @Resource(name = "userSignService")
    UserSignService userSignService;

    // 로그인
    @GetMapping("/login")
    public ResponseEntity<?> getSsoLoginUrls(@RequestParam(name = "email", required = false) String email
                                            , @RequestParam(name = "registrationId", required = false)String registrationId){

        if(email == null || registrationId == null){
            return ResponseEntity.badRequest().body("Email and registrationId are required");
        }

        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("registrationId", registrationId);

        UserAccountVO userAccountVO = userSignService.ssoLoginSelect(params);

        if(userAccountVO != null){

            return ResponseEntity.ok().body(Map.of(
                    "status", "login"
                    , "message", "User already exists"
                    , "userId", userAccountVO.getUserId()
                    , "userRole", userAccountVO.getUserRole()
                    , "email", userAccountVO.getUserProfileVO().getEmail()
                    , "registrationId", userAccountVO.getRegistrationId()));
        } else {
            Map<String, String> signupInfo = new HashMap<>();
            signupInfo.put("status", "signup");
            signupInfo.put("email", email);
            signupInfo.put("registrationId", registrationId);

            return ResponseEntity.ok().body(signupInfo);
        }
    }

    // 엑세스 토큰 체크
    @GetMapping("/checkToken")
    public ResponseEntity<?> checkToken(@CookieValue("access_token") String accessToken){
        if(jwtService.validateToken(accessToken, false)){
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // 회원가입
    @PostMapping("/sign1/domSignup")
    public ResponseEntity<?> userSignSso(@RequestBody Map<String, Object> signupData
                                        , @CookieValue("access_token") String accessToken){
        if(!jwtService.validateToken(accessToken, false)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            UserAccountVO userAccountVO = new UserAccountVO();
            UserProfileVO userProfileVO = new UserProfileVO();

            userAccountVO.setUserId((String) signupData.get("userId"));
            userAccountVO.setUserRole((String) signupData.get("userRole"));
            userAccountVO.setRegistrationId((String) signupData.get("registrationId"));

            userProfileVO.setEmail((String) signupData.get("email"));

            userSignService.userSignSso(userAccountVO, userProfileVO);
            return ResponseEntity.ok().body(Map.of("message", "Signup successful"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }

    }


}
