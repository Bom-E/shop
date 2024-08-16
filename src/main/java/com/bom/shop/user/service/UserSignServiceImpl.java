package com.bom.shop.home.service;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("userSignService")
public class UserSignServiceImpl implements UserSignService{

    @Autowired
    private SqlSessionTemplate sqlSession;


}
