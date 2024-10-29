package com.bom.shop.user.service;

import com.bom.shop.user.vo.RefreshTokenManageVO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("refreshTokenMangeService")
public class RefreshTokenMangeServiceImpl implements RefreshTokenMangeService{

    @Autowired
    private SqlSessionTemplate sqlSession;

    @Override
    public void insertRefreshToken(RefreshTokenManageVO refreshTokenManageVO) {
        sqlSession.insert("tokenMapper.insertRefreshToken", refreshTokenManageVO);
    }

    @Override
    public RefreshTokenManageVO findByUserId(String userId) {
        return sqlSession.selectOne("tokenMapper.findByUserId", userId);
    }

    @Override
    public void deleteByUserId(String userId) {
        sqlSession.delete("tokenMapper.deleteByUserId", userId);
    }

    @Override
    public void deleteExpiredTokens() {
        sqlSession.delete("tokenMapper.deleteExpiredTokens");
    }

    @Override
    public boolean validateToken(RefreshTokenManageVO refreshTokenManageVO) {
        return sqlSession.selectOne("tokenMapper.validateToken", refreshTokenManageVO);
    }
}
