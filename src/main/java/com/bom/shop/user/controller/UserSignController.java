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

import java.util.Map;

@Controller
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class UserSignController {

    @Resource(name = "userSignService")
    UserSignService userSignService;

    // 일반 회원가입
    @PostMapping("/sign1/defaultSignup")
    public ResponseEntity<?> userSignSso(@RequestBody Map<String, Object> signupData){

        try {
            validateDefaultSignupData(signupData);

            UserAccountVO userAccountVO = createDefaultUserAccount(signupData);
            UserProfileVO userProfileVO = createUserProfile(signupData);

            userSignService.defaultUserSign(userAccountVO, userProfileVO);

            return ResponseEntity.ok().body(Map.of("message", "Signup successful"));

        } catch (Exception e) {
            return handleSignupError(e);
        }

    }

    // 오어서2 회원가입
    @PostMapping("/sign1/ssoSignup")
    public ResponseEntity<?> oauth2Signup(@RequestBody Map<String, Object> signupData){

        try {
            validateOAuth2SighupData(signupData);

            UserAccountVO userAccountVO = createOAuth2UserAccount(signupData);
            UserProfileVO userProfileVO = createUserProfile(signupData);

            userSignService.ssoUserSign(userAccountVO, userProfileVO);

            return ResponseEntity.ok().body(Map.of("message", "Signup successful"));

        } catch (Exception e) {
            return handleSignupError(e);
        }
    }

    private UserAccountVO createOAuth2UserAccount(Map<String, Object> signupData){
        UserAccountVO userAccountVO = new UserAccountVO();
        userAccountVO.setUserId((String) signupData.get("userId"));
        userAccountVO.setRegistrationId((String) signupData.get("registrationId"));
        userAccountVO.setUserRole("USER");

        return userAccountVO;
    }

    private UserAccountVO createDefaultUserAccount(Map<String, Object> signupData){
        UserAccountVO userAccountVO = new UserAccountVO();
        userAccountVO.setUserId((String) signupData.get("userId"));
        userAccountVO.setUserPw((String) signupData.get("userPw"));
        userAccountVO.setUserRole("USER");

        return userAccountVO;
    }

    private UserProfileVO createUserProfile(Map<String, Object> signupData){
        UserProfileVO userProfileVO = new UserProfileVO();
        userProfileVO.setUserId((String)signupData.get("userId"));
        userProfileVO.setUserName((String)signupData.get("userName"));
        userProfileVO.setGender((String)signupData.get("gender"));
        userProfileVO.setEmail((String)signupData.get("email"));
        userProfileVO.setUserTel((String)signupData.get("userTel"));
        userProfileVO.setBirthDate((String)signupData.get("birthDate"));

        return userProfileVO;
    }

    private void validateOAuth2SighupData(Map<String, Object> signupData){
        if(signupData.get("registrationId") == null){
            throw new IllegalArgumentException("Registration ID is required for OAuth2 signup");
        }
    }

    private void validateDefaultSignupData(Map<String, Object> signupData){
        String userPw = (String) signupData.get("userPw");
        if(userPw == null || userPw.trim().isEmpty()){
            throw new IllegalArgumentException("Password is required for normal signup");
        }
    }

    private ResponseEntity<?> handleSignupError(Exception e){
        String errorMessage = e.getMessage() != null ? e.getMessage() : "An unknown error occurred";
        return ResponseEntity.badRequest().body(Map.of("error", errorMessage));
    }
}
