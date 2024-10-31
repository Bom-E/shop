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

        return sqlSession.selectOne("authMapper.ssoLoginSelect", params);
    }

    @Override
    public UserAccountVO defaultLoginSelect(UserAccountVO loginData) {
        UserAccountVO checkData = sqlSession.selectOne("authMapper.playLoginDataCheck", loginData.getUserId());

        if(checkData != null && passwordEncoder.matches(loginData.getUserPw(), checkData.getUserPw())){
            System.out.println("Input PW :" + loginData.getUserPw());
            System.out.println("Stored Pw : " + checkData.getUserPw());
            System.out.println("Matches :" + passwordEncoder.matches(loginData.getUserPw(), checkData.getUserPw()));

            return sqlSession.selectOne("authMapper.defaultLoginSelect", loginData);
        }
        return null;
    }

    @Override
    public UserAccountVO playLoginDataCheck(UserAccountVO userAccountVO) {

        return sqlSession.selectOne("authMapper.playLoginDataCheck", userAccountVO);
    }

    @Override
    public UserAccountVO findByEmail(String email) {
        return sqlSession.selectOne("authMapper.findByEmail", email);
    }

}
