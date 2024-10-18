package com.bom.shop.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class AbstractLoginHandler {

    protected void commonSuccessProcessing(HttpServletRequest request, HttpServletResponse response, Authentication authentication){
        // 로그인 성공 시 공통 처리 로직
        // 예: 로그 기록, 마지막 로그인 시간 업데이트 등
        System.out.println("User " + authentication.getName() + " logged in successfully");
    }

    protected void commonFailureProcessing(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception){
        // 로그인 실패 시 공통 처리 로직
        // 예: 로그 기록, 실패 횟수 증가 등
        System.out.println("Login failed " + exception.getMessage());
    }

    protected String determineTargetUrl(Authentication authentication){
        // 로그인 성공 후 리다이렉트 할 URL 결정
        // 예: 사용자 역할에 따라 다른 페이지로 리다이렉트
        return "/";
    }
}
