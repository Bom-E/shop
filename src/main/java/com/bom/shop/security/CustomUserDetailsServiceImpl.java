package com.bom.shop.security;

import com.bom.shop.user.service.UserAuthService;
import com.bom.shop.user.vo.UserAccountVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service("customUserDetailsService")
public class CustomUserDetailsServiceImpl extends DefaultOAuth2UserService implements UserDetailsService{

    private final UserAuthService userAuthService;

    @Autowired
    public CustomUserDetailsServiceImpl(UserAuthService userAuthService){
        this.userAuthService = userAuthService;
    }

    // sso
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                                                    .getProviderDetails()
                                                    .getUserInfoEndpoint()
                                                    .getUserNameAttributeName();

        String email = extractEmail(oauth2User, registrationId);

        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("registrationId", registrationId);

        UserAccountVO userAccountVO = userAuthService.ssoLoginSelect(params);

        if(userAccountVO == null){
            throw new OAuth2AuthenticationException("User not found");
        }

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + userAccountVO.getUserRole()))
                , oauth2User.getAttributes()
                , userNameAttributeName
        );
    }

    // 일반
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccountVO userAccountVO = new UserAccountVO();
        userAccountVO.setUserId(username);

        UserAccountVO user = userAuthService.playLoginDataCheck(userAccountVO);

        if(user == null){
            throw new UsernameNotFoundException("User not found with userId: " + username);
        }
        return User.builder()
                .username(user.getUserId())
                .password(user.getUserPw())
                .authorities(Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + userAccountVO.getUserRole())))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }

    private String extractEmail(OAuth2User oauth2User, String registrationId){
        switch (registrationId) {
            case "google":
                return oauth2User.getAttribute("email");
            case "naver":
                Map<String, Object> naverAttributes = oauth2User.getAttribute("response");
                return (String) naverAttributes.get("email");
            case "kakao":
                Map<String, Object> kakaoAttributes = oauth2User.getAttribute("kakao_account");
                return (String) kakaoAttributes.get("email");
            default:
                throw new OAuth2AuthenticationException(
                        "Unsupported registration ID: " + registrationId
                );
        }
    }
}
