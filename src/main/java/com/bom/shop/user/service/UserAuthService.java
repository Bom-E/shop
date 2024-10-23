package com.bom.shop.user.service;

import com.bom.shop.user.vo.UserAccountVO;
import com.bom.shop.user.vo.UserProfileVO;

import java.util.List;
import java.util.Map;


public interface UserAuthService {

    // sso 로그인
    UserAccountVO ssoLoginSelect(Map<String, String> params);

    // 일반 로그인
    UserAccountVO normalLoginSelect(String userId);

    // refreshToken 갱신
    List<String> getRolesByEmail(UserProfileVO userProfileVO);

}
