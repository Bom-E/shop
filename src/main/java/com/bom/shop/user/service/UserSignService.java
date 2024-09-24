package com.bom.shop.user.service;

import com.bom.shop.user.vo.UserAccountVO;
import com.bom.shop.user.vo.UserProfileVO;

import java.util.List;
import java.util.Map;

public interface UserSignService {

    // sso 회원가입
    void userSignSso(UserAccountVO userAccountVO, UserProfileVO userProfileVO);

    // 로그인
    UserAccountVO ssoLoginSelect(Map<String, String> params);

    // refreshToken 갱신
    List<String> getRolesByEmail(UserProfileVO userProfileVO);

    // 중복가입 방지
    boolean joinCheck(String email);
}
