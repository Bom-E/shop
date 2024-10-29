package com.bom.shop.user.service;

import com.bom.shop.user.vo.RefreshTokenManageVO;
import com.bom.shop.user.vo.UserAccountVO;

public interface RefreshTokenMangeService {

    void insertRefreshToken(RefreshTokenManageVO refreshTokenManageVO);

    RefreshTokenManageVO findByUserId(String userId);

    void deleteByUserId(String userId);

    void deleteExpiredTokens();

    boolean validateToken(RefreshTokenManageVO refreshTokenManageVO);
}
