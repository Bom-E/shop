package com.bom.shop.user.service;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("userSignService")
public class UserAuthServiceImpl implements UserAuthService {

    @Autowired
    private SqlSessionTemplate sqlSession;


}
