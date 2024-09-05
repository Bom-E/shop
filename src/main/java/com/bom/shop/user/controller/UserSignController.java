package com.bom.shop.user.controller;

import com.bom.shop.user.service.UserSignService;
import com.bom.shop.user.vo.UserProfileVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/userSign")
public class UserSignController {
    @Resource(name = "userSignService")
    UserSignService userSignService;

    // sso 로그인 구현
//    @PostMapping("/")

    // 리프레시 토큰에서 이메일로 유저 역할 조회
    @PostMapping("/userSign/sign1")
    public List<String> userSignWithGoogle(UserProfileVO userProfileVO){
        // 추후 로그인 기능 구현 후 이메일로 역할 조회 하는 건 여기로 들어 올 예정.

        return userSignService.getRolesByEmail(userProfileVO);
    }


}
