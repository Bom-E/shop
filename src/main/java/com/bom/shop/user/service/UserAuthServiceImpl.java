package com.bom.shop.user.service;

import com.bom.shop.user.vo.UserAccountVO;
import com.bom.shop.user.vo.UserProfileVO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service("userAuthService")
public class UserAuthServiceImpl implements UserAuthService {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setPasswordEncoder (PasswordEncoder passwordEncoder){
        this.passwordEncoder = passwordEncoder;
    }

    // 소셜 로그인
    @Override
    public UserAccountVO ssoLoginSelect(Map<String, String> params) {

        return sqlSession.selectOne("userMapper.ssoLoginSelect", params);
    }

    @Override
    public UserAccountVO normalLoginSelect(String userId) {
        return sqlSession.selectOne("userMapper.normalLoginSelect", userId);
    }


    @Override
    public List<String> getRolesByEmail(UserProfileVO userProfileVO) {
        return sqlSession.selectList("userMapper.getRolesByEmail", userProfileVO);
    }

}
