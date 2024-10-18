package com.bom.shop.security;

import com.bom.shop.user.service.UserSignService;
import com.bom.shop.user.vo.UserAccountVO;
import com.bom.shop.user.vo.UserProfileVO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service("defaultOAuth2UserService")
public class CustomUserDetailsServiceImpl extends DefaultOAuth2UserService implements UserDetailsService{


    private UserSignService userSignService;

    @Autowired
    public CustomUserDetailsServiceImpl(UserSignService userSignService){
        this.userSignService = userSignService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                                    .getUserInfoEndpoint().getUserNameAttributeName();

        Map<String, Object> attributes = oauth2User.getAttributes();

        String email = (String) attributes.get("email");

        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("registrationId", registrationId);

        UserAccountVO userAccountVO = userSignService.ssoLoginSelect(params);

        if(userAccountVO == null){
            throw new UsernameNotFoundException("User not found");
        }

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + userAccountVO.getUserRole()))
                , attributes
                , userNameAttributeName
        );
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        UserAccountVO userAccountVO = userSignService.normalLoginSelect(userId);
        if(userAccountVO == null){
            throw new UsernameNotFoundException("User not found with userId: " + userId);
        }
        return new User(userAccountVO.getUserId()
                        , userAccountVO.getUserPw()
                        , Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userAccountVO.getUserRole())));
    }
}
