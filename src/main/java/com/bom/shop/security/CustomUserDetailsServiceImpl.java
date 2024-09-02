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
    // 이 부분은 추후 selectUserOne 을 추상화 한 클래스의 한 메소드로 변경 예정

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        UserProfileVO user = sqlSession.selectOne("userMapper.selectUserOne", userEmail);
        if(user == null){
            throw new UsernameNotFoundException("User not found with email: " + userEmail);
        }
        return createUserDetails(user);
    }

    private UserDetails createUserDetails(UserProfileVO userProfileVO){
        // 이 부분들도 클래스 구현 후 바꿔서 리턴은 변수만 리턴해줄 예정.
        return org.springframework.security.core.userdetails.User.builder()
                .username(userProfileVO.getUserEmail())
                .password(userProfileVO.getUserAccountVO().getUserPw())
                .roles(userProfileVO.getUserAccountVO().getUserRole())
                .build();
    }
}
