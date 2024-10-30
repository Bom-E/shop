package com.bom.shop.user.service;

import com.bom.shop.user.vo.UserAccountVO;
import com.bom.shop.user.vo.UserProfileVO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("userSignService")
public class UserSignServiceImpl implements UserSignService{

    @Autowired
    private SqlSessionTemplate sqlSession;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setPasswordEncoder (PasswordEncoder passwordEncoder){
        this.passwordEncoder = passwordEncoder;
    }

    // sso 회원가입
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void ssoUserSign(UserAccountVO userAccountVO, UserProfileVO userProfileVO) {
        sqlSession.insert("signMapper.signUserWithSso1", userAccountVO);
        sqlSession.insert("signMapper.signUserWithSso2", userProfileVO);
    }

    // 일반 회원가입
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void defaultUserSign(UserAccountVO userAccountVO, UserProfileVO userProfileVO) {
        String securityPw = passwordEncoder.encode(userAccountVO.getUserPw());
        userAccountVO.setUserPw(securityPw);

        sqlSession.insert("signMapper.userSignInsert1", userAccountVO);
        sqlSession.insert("signMapper.userSignInsert2", userProfileVO);
    }

    @Override
    public boolean joinCheck(String email) {
        return sqlSession.selectOne("signMapper.joinCheck", email);
    }

}
