package com.bom.shop.security;

import com.bom.shop.security.jwtFacadePattern.JwtService;
import com.bom.shop.security.jwtFacadePattern.JwtServiceFactory;
import com.bom.shop.user.service.UserSignService;
import com.bom.shop.utility.CookieUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtServiceFactory jwtServiceFactory;
    private final ObjectMapper objectMapper;
    private final UserSignService userSignService;
    private final CookieUtil cookieUtil;

    public SecurityConfig(JwtServiceFactory jwtServiceFactory, ObjectMapper objectMapper, UserSignService userSignService, CookieUtil cookieUtil){
        this.jwtServiceFactory = jwtServiceFactory;
        this.objectMapper = objectMapper;
        this.userSignService = userSignService;
        this.cookieUtil = cookieUtil;
    }

    @Bean
    public TokenAuthenticationSuccessHandler tokenAuthenticationSuccessHandler(){
        JwtService jwtService = jwtServiceFactory.createJwtService();
        return new TokenAuthenticationSuccessHandler(jwtService, objectMapper, userSignService, cookieUtil);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        JwtService jwtService = jwtServiceFactory.createJwtService();

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable) // 혹시 나중에 시간 여유가 되면 추가 보안 설정 해보기
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/index.html", "/login/**", "/oauth2/**", "/auth/**", "/api/**").permitAll()
                        // .requestMatchers("/user/**").hasRole("USER")
                        // .requestMatchers("/admin/**").hasRole("ADMIN")
                        // .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/auth/login")
                        .successHandler(tokenAuthenticationSuccessHandler())
                        .failureHandler(new TokenAuthenticationFailureHandler(objectMapper))
                )
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authorization -> authorization
                                .baseUri("/oauth2/authorization"))
                        .redirectionEndpoint(redirection -> redirection
                                .baseUri("/login/oauth2/code/*"))
                        .successHandler(tokenAuthenticationSuccessHandler())
                        .failureHandler(new TokenAuthenticationFailureHandler(objectMapper))
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                    .authenticationEntryPoint((request, response, authException) -> {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write("Unauthorized:" + authException.getMessage());
                    })
                )
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(){
        JwtService jwtService = jwtServiceFactory.createJwtService();
        return new JwtAuthenticationFilter(jwtService);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
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
                , "/css/**"
                , "images/**"
                , "/favicon.ico"
                , "index.html"
        );
    }
}
