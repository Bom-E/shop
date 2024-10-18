package com.bom.shop.user.service;

import com.bom.shop.security.PasswordEncoderConfig;
import com.bom.shop.user.service.UserSignService;
import com.bom.shop.user.vo.UserAccountVO;
import com.bom.shop.user.vo.UserProfileVO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("userSignService")
public class UserSignServiceImpl implements UserSignService {

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
    public void userSignSso(UserAccountVO userAccountVO, UserProfileVO userProfileVO) {
        sqlSession.insert("userMapper.signUserWithSso1", userAccountVO);
        sqlSession.insert("userMapper.signUserWithSso2", userProfileVO);
    }

    // 일반 회원가입
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void userSignNormal(UserAccountVO userAccountVO, UserProfileVO userProfileVO) {
        String securityPw = passwordEncoder.encode(userAccountVO.getUserPw());
        userAccountVO.setUserPw(securityPw);

        sqlSession.insert("userMapper.userSignInsert1", userAccountVO);
        sqlSession.insert("userMapper.userSignInsert2", userProfileVO);
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

    @Override
    public boolean joinCheck(String email) {
        return sqlSession.selectOne("userMapper.joinCheck", email);
    }

}
