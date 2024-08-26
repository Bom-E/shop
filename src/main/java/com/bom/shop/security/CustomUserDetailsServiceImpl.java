package com.bom.shop.security;

import com.bom.shop.user.vo.UserProfileVO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private SqlSessionTemplate sqlSession;

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        UserProfileVO user = sqlSession.selectOne("userMapper.selectUserOne", userEmail);
        if(user == null){
            throw new UsernameNotFoundException("User not found with email: " + userEmail);
        }
        return createUserDetails(user);
    }

    private UserDetails createUserDetails(UserProfileVO userProfileVO){
        return org.springframework.security.core.userdetails.User.builder()
                .username(userProfileVO.getUserEmail())
                .password(userProfileVO.getUserAccountVO().getUserPw())
                .roles(userProfileVO.getUserAccountVO().getUserRole())
                .build();
    }
}
