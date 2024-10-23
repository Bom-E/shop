package com.bom.shop.user.controller;

import com.bom.shop.user.service.UserAuthService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/userSign")
public class UserSignController {
    @Resource(name = "userSignService")
    UserAuthService userAuthService;


}
