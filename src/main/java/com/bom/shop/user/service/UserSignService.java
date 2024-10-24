package com.bom.shop.user.service;

import com.bom.shop.user.vo.UserAccountVO;
import com.bom.shop.user.vo.UserProfileVO;

public interface UserSignService {

    // sso 회원가입
    void ssoUserSign(UserAccountVO userAccountVO, UserProfileVO userProfileVO);

    // 일반 회원가입
    void defaultUserSign(UserAccountVO userAccountVO, UserProfileVO userProfileVO);

    // 중복가입 방지
    boolean joinCheck(String email);

}
