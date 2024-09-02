package com.bom.shop.security;

import com.bom.shop.security.jwtFacadePattern.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtService jwtService;
    private final ObjectMapper objectMapper;
    private final CustomUserDetailsServiceImpl userDetailsService;

    public SecurityConfig(JwtService jwtService, ObjectMapper objectMapper, CustomUserDetailsServiceImpl userDetailsService){
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable) // 혹시 나중에 시간 여유가 되면 추가 보안 설정 해보기
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login/**").permitAll()
                        // .requestMatchers("/user/**").hasRole("USER")
                        // .requestMatchers("/admin/**").hasRole("ADMIN")
                        // .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .successHandler(new MixAuthenticationSuccessHandler(jwtService, objectMapper))
                        .failureHandler(new MixAuthenticationFailureHandler(objectMapper))
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(new MixAuthenticationSuccessHandler(jwtService, objectMapper))
                        .failureHandler(new MixAuthenticationFailureHandler(objectMapper))
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3030"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return web -> web.ignoring().requestMatchers(
                "/assets/**"
                , "/js/**"
                , "/favicon.ico"
        );
    }
}
