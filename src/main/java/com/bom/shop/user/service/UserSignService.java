package com.bom.shop.user.service;

import com.bom.shop.user.vo.UserAccountVO;
import com.bom.shop.user.vo.UserProfileVO;

import java.util.List;
import java.util.Map;


public interface UserSignService {

    // sso 회원가입
    void userSignSso(UserAccountVO userAccountVO, UserProfileVO userProfileVO);

    // 일반 회원가입
    void userSignNormal(UserAccountVO userAccountVO, UserProfileVO userProfileVO);

    // sso 로그인
    UserAccountVO ssoLoginSelect(Map<String, String> params);

    // 일반 로그인
    UserAccountVO normalLoginSelect(String userId);

    // refreshToken 갱신
    List<String> getRolesByEmail(UserProfileVO userProfileVO);

    // 중복가입 방지
    boolean joinCheck(String email);
}
