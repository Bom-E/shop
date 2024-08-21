package com.bom.shop.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class Oauth2Config {
    @Autowired
    private OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .oidcUserService(oidcUserService)
                        )
                        .successHandler((request, response, authentication) -> {
                            if(authentication instanceof OAuth2AuthenticationToken){
                                OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
                                String registrationId = token.getAuthorizedClientRegistrationId();

                                // 프론트에서 받은 로그인 타입
                                String frontendLoginType = request.getParameter("loginType");

                                // 서버에서 한 번 더 체크
                                if(!registrationId.equalsIgnoreCase(frontendLoginType)){
                                    // 불일치 시 프론트로 데이터 넘겨줄거임. 하지만 지금 아직 구현 다 안 해서 나중에 할 거임.
                                }
                                // 인증 성공 후 처리 로직
                            }
                        })
                );

        return http.build();
    }
}
