package com.bom.shop.security.jwtFacadePattern;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Service("jwtTokenGenerator")
public class JwtTokenGenerator {
    @Autowired
    private JwtProperties jwtProperties;

    private final Key key;
    private final Key refreshKey;

    public JwtTokenGenerator(JwtProperties jwtProperties){
        this.jwtProperties = jwtProperties;
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));
        this.refreshKey = Keys.hmacShaKeyFor(jwtProperties.getRefreshSecretKey().getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String userEmail, Collection<? extends GrantedAuthority> authorities){
        return Jwts.builder()
                .setSubject(userEmail)
                .claim("roles", authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getAccessExpireTime()))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(String userEmail){
        return Jwts.builder()
                .setSubject(userEmail)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getRefreshExpireTime()))
                .signWith(refreshKey)
                .compact();
    }
}
