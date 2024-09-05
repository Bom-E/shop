package com.bom.shop.user.service;

import com.bom.shop.user.service.UserSignService;
import com.bom.shop.user.vo.UserAccountVO;
import com.bom.shop.user.vo.UserProfileVO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("userSignService")
public class UserSignServiceImpl implements UserSignService {

    @Autowired
    private SqlSessionTemplate sqlSession;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void joinUser(UserAccountVO userAccountVO, UserProfileVO userProfileVO) {
        sqlSession.insert("userMapper.signUserWithSso1", userAccountVO);
        sqlSession.insert("userMapper.signUserWithSso2", userProfileVO);
    }

    @Override
    public List<String> getRolesByEmail(UserProfileVO userProfileVO) {
        return sqlSession.selectList("userMapper.getRolesByEmail", userProfileVO);
    }

    @Override
    public boolean joinCheck(String email) {
        return sqlSession.selectOne("userMapper.joinCheck", email);
    }

    @Override
    public UserAccountVO findOneByEmail(String email) {
        return sqlSession.selectOne("userMapper.findOneByEmail", email);
    }
}
