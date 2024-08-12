package com.bom.shop.home.controller;

import com.bom.shop.home.service.UserSignService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/userSign")
public class UserSignController {
    @Resource(name = "userSignService")
    UserSignService userSignService;


}
