package com.bom.shop.security.jwtFacadePattern;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service("jwtTokenGenerator")
public class JwtTokenGenerator {
    private final JwtProperties jwtProperties;
    private final Key key;
    private final Key refreshKey;

    @Autowired
    public JwtTokenGenerator(@Qualifier("jwtProperties") JwtProperties jwtProperties){
        this.jwtProperties = jwtProperties;
        System.out.println("JwtTokenGenerator constructor");
        System.out.println("Secret Key: " + jwtProperties.getSecretKey());
        System.out.println("Refresh Key: " + jwtProperties.getRefreshSecretKey());

        if(jwtProperties.getSecretKey() == null || jwtProperties.getRefreshSecretKey() == null){
            throw new IllegalStateException("Secret keys are not properly initialized");
        }

        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));
        this.refreshKey = Keys.hmacShaKeyFor(jwtProperties.getRefreshSecretKey().getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(String email, Collection<? extends GrantedAuthority> authorities){

        return Jwts.builder()
                .setSubject(email)
                .claim("roles", authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getAccessExpireTime()))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(String email){
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getRefreshExpireTime()))
                .signWith(refreshKey)
                .compact();
    }
}