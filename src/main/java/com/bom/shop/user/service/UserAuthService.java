package com.bom.shop.user.service;

import com.bom.shop.user.vo.UserAccountVO;
import com.bom.shop.user.vo.UserProfileVO;

import java.util.List;
import java.util.Map;


public interface UserAuthService {

    // sso 로그인
    UserAccountVO ssoLoginSelect(Map<String, String> params);

    // 일반 로그인 이용자 정보 로드
    UserAccountVO defaultLoginSelect(UserAccountVO loginData);

    // 일반 로그인 로그인용
    UserAccountVO playLoginDataCheck(UserAccountVO userAccountVO);

    // refreshToken 갱신
    List<String> getRolesByEmail(UserProfileVO userProfileVO);

}
