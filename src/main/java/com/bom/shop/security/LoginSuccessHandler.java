package com.bom.shop.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private RequestCache requestCache = new HttpSessionRequestCache();
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();


    // 로그인 성공 시 자동으로 호출 되는 메소드
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        SavedRequest savedRequest = requestCache.getRequest(request, response);

        // .defaultSuccessUrl("/") 이것과 조금 다름
        // 아래건 무조건 가는거라서 if 문이 있어야 이전 페이지로 이동 할 수 있음
        // 로그인 성공 시 이동할 페이지
        setDefaultTargetUrl("/");


        // 로그인 시 필요한 코드 여기서 작성
        // 예: 세션에 데이터 저장, 아이피 열람
        HttpSession session = request.getSession();
        // session.setAttribute();
        // session.getAttribute();

        //로그인 정보를 통한 로직 구성
        User user = (User) authentication.getPrincipal();
        List<GrantedAuthority> authorityList = new ArrayList<>(user.getAuthorities());
        List<String> aList = new ArrayList<>();

        for(GrantedAuthority authority: authorityList){
            aList.add(authority.getAuthority());
        }

        boolean b = aList.contains("ADMIN");

        if(b){
            redirectStrategy.sendRedirect(request, response, "/admin/regItem");
        }

        // 이전 페이지 혹은 가려던 페이지가 있는 경우
        if(savedRequest != null){
            String targetUrl = savedRequest.getRedirectUrl();
            redirectStrategy.sendRedirect(request, response, targetUrl);
        }else{
            redirectStrategy.sendRedirect(request, response, getDefaultTargetUrl());
        }
    }
}
