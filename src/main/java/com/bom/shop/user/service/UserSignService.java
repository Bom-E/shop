package com.bom.shop.user.service;

import com.bom.shop.user.vo.UserProfileVO;

import java.util.List;

public interface UserSignService {

    List<String> getRolesByEmail(UserProfileVO userProfileVO);
}
