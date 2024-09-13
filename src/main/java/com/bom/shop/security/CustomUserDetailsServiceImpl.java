package com.bom.shop.security;

import com.bom.shop.user.service.UserSignService;
import com.bom.shop.user.vo.UserAccountVO;
import com.bom.shop.user.vo.UserProfileVO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service("userDetailsService")
public class CustomUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserSignService userSignService;
    // 이 부분은 추후 selectUserOne 을 추상화 한 클래스의 한 메소드로 변경 예정

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserAccountVO userAccountVO = userSignService.findOneByEmail(email);
        if(userAccountVO == null){
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        return createUserDetails(userAccountVO);
    }

    private UserDetails createUserDetails(UserAccountVO userAccountVO){

        return org.springframework.security.core.userdetails.User.builder()
                .username(userAccountVO.getUserProfileVO().getEmail())
                .password(userAccountVO.getUserPw())
                .authorities(Collections.singleton(new SimpleGrantedAuthority("ROLE_" + userAccountVO.getUserRole())))
                .build();
    }
}
