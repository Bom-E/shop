package com.bom.shop.user.service;

import com.bom.shop.user.vo.UserAccountVO;
import com.bom.shop.user.vo.UserProfileVO;

import java.util.List;

public interface UserSignService {

    // sso 회원가입
    void joinUser(UserAccountVO userAccountVO, UserProfileVO userProfileVO);
    // refreshToken 갱신
    List<String> getRolesByEmail(UserProfileVO userProfileVO);

    // 중복가입 방지
    boolean joinCheck(String email);

    UserAccountVO findOneByEmail(String email);
}
