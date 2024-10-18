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
import java.util.logging.Logger;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class UserSignController {
    @Resource(name = "jwtService")
    JwtService jwtService;
    @Resource(name = "userSignService")
    UserSignService userSignService;

    // 로그인
    @GetMapping("/login")
    public ResponseEntity<?> getSsoLoginUrls(@RequestParam(name = "email", required = false) String email
                                            , @RequestParam(name = "registrationId", required = false)String registrationId
                                            , @RequestParam(name = "userId", required = false) String userId){

//        if(isNormalLogin(email, registrationId, userId)){
//            return handleNormalLogin(userId);
//        } else if(isSsoLogin(email, registrationId)){
//            return handleSsoLogin(email, registrationId);
//        } else {
//            return ResponseEntity.badRequest().body("Invalid parameters");
//        }

        if(email == null && registrationId == null){
            UserAccountVO userAccountVO = userSignService.normalLoginSelect(userId);
            if(userAccountVO != null){

                return ResponseEntity.ok().body(Map.of(
                        "status", "login"
                        , "message", "User already exists"
                        , "userId", userAccountVO.getUserId()
                        , "userRole", userAccountVO.getUserRole()
                        , "userName", userAccountVO.getUserProfileVO().getUserName()));
            } else {
                Map<String, String> signupInfo = new HashMap<>();
                signupInfo.put("status", "signup");
                signupInfo.put("userId", email);

                return ResponseEntity.ok().body(signupInfo);
            }
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

            String userId = (String) signupData.get("userId");
            String registrationId = (String) signupData.get("registrationId");
            String userPw = (String) signupData.get("userPw");

            System.out.println("@@@@@@@@@@@@@@@@@registrationId=" + registrationId);
            System.out.println("@@@@@@@@@@@@@@@@@userPw=" + userPw);

            userAccountVO.setUserId(userId);
            userAccountVO.setUserRole("USER");

            userProfileVO.setUserId(userId);
            userProfileVO.setUserName((String) signupData.get("userName"));
            userProfileVO.setGender((String) signupData.get("gender"));
            userProfileVO.setEmail((String) signupData.get("email"));
            userProfileVO.setUserTel((String) signupData.get("userTel"));
            userProfileVO.setBirthDate((String) signupData.get("birthDate"));

            if(registrationId != null && !registrationId.isEmpty()){
                // sso 회원가입
                if(accessToken == null || !jwtService.validateToken(accessToken, false)){
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                }
                userAccountVO.setRegistrationId(registrationId);
                userSignService.userSignSso(userAccountVO, userProfileVO);
            } else if(userPw != null && !userPw.isEmpty()){
                userAccountVO.setUserPw(userPw);
                userSignService.userSignNormal(userAccountVO, userProfileVO);
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid signup data"));
            }

            return ResponseEntity.ok().body(Map.of("message", "Signup successful"));
        } catch (Exception e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : "An unknown error occurred";
            return ResponseEntity.badRequest().body(Map.of("error", errorMessage));
        }

    }

}
