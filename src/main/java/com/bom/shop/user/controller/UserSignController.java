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

    @Resource(name = "jwtService")
    JwtService jwtService;
    @Resource(name = "userSignService")
    UserSignService userSignService;

    // 회원가입 추후 리펙토링으로 일반 회원가입과 오어서 회원가입 분리하기
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

                // 일반 회원가입
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
