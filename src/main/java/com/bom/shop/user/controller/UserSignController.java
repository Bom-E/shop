package com.bom.shop.user.controller;

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
    @Resource(name = "userSignService")
    UserSignService userSignService;

    // 로그인
    @GetMapping("/sign1")
    public ResponseEntity<?> getSsoLoginUrls(@RequestParam(name = "email", required = false) String email
                                            , @RequestParam(name = "registrationId", required = false)String registrationId){
        UserAccountVO userAccountVO = userSignService.ssoLoginSelect(email, registrationId);

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

    // 회원가입
//    @PostMapping("/signup")
//    public ResponseEntity<?> userSignSso(@RequestBody UserAccountVO userAccountVO){
//
//        try {
//            userAccountVO.setUserRole();
//        } catch () {
//
//        }
//
//    }


}
