package com.bom.shop.user.controller;

import com.bom.shop.user.service.UserSignService;
import com.bom.shop.user.vo.UserAccountVO;
import com.bom.shop.user.vo.UserProfileVO;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/userSign")
@CrossOrigin(origins = "http://localhost:3000")
public class UserSignController {
    @Resource(name = "userSignService")
    UserSignService userSignService;

    // 로그인 혹은 회원가입
    @PostMapping("/sign1")
    public ResponseEntity<?> userSignSso(UserProfileVO userProfileVO){

        UserAccountVO user = userSignService.findOneByEmail(userProfileVO.getEmail());

        if(user != null){

            return ResponseEntity.ok("User already exists");
        } else {

            Map<String, String> response = new HashMap<>();
            response.put("email", userProfileVO.getEmail());
            response.put("registrationId", user.getRegistrationId());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

    }


}
