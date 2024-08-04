package com.bom.shop.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

//인증 인가에 대한 설정을 위한 클래스
@EnableWebSecurity
@Configuration
public class SecurityConfig {
    @Autowired
    private LoginFailHandler loginFailHandler;

    @Autowired
    private LoginSuccessHandler loginSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity security) throws Exception {

        security.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        c -> {
                            c.anyRequest().permitAll();

//                            c.requestMatchers(
//                                        new AntPathRequestMatcher("/"),
//                                        new AntPathRequestMatcher("/item/list"),
//                                        new AntPathRequestMatcher("/member/loginForm"),
//                                        new AntPathRequestMatcher("/member/join"),
//                                        new AntPathRequestMatcher("/member/login")
//                                    ).permitAll()
//                                .requestMatchers(
//                                        new AntPathRequestMatcher("/admin/**")
//                                ).hasRole("ADMIN")
//                                .anyRequest().authenticated();
                        }
                )
                .formLogin(
                        formLogin -> {
                            formLogin.loginPage("/member/loginForm")
                                    .loginProcessingUrl("/member/login")
                                    //.defaultSuccessUrl("/")
                                    //.failureUrl("/member/loginForm")
                                    .usernameParameter("memberId")
                                    .passwordParameter("memberPw")
                                    //로그인 성공 시 실행시킬 클래스의 객체
                                    .successHandler(loginSuccessHandler)
                                    //로그인 실패 시 실행시킬 클래스의 객체
                                    .failureHandler(loginFailHandler);
                        }
                )
                .logout(
                        logout -> {

                        }
                );


        return security.build();
    }

    //정적인 자원은 security가 인증 및 인가 관리에서 제외하도록 설정
    //정적인 파일 : .js , .css, 이미지
    //resources 폴더 밑에 있는 모든 파일들
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return web -> web.ignoring().requestMatchers(
                new AntPathRequestMatcher("/assets/**"),
                new AntPathRequestMatcher("/css/**"),
                new AntPathRequestMatcher("/js/**"),
                new AntPathRequestMatcher("/images/**"),
                new AntPathRequestMatcher("/favicon.ico")
        );
    }



}


