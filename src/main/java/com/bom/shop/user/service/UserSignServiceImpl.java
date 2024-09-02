package com.bom.shop.user.service;

import com.bom.shop.user.service.UserSignService;
import com.bom.shop.user.vo.UserProfileVO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("userSignService")
public class UserSignServiceImpl implements UserSignService {

    @Autowired
    private SqlSessionTemplate sqlSession;


    @Override
    public List<String> getRolesByEmail(UserProfileVO userProfileVO) {
        return sqlSession.selectList("getRolesByEmail", userProfileVO);
    }
}
