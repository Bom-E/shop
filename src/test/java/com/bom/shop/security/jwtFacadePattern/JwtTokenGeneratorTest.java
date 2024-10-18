package com.bom.shop.security.jwtFacadePattern;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class JwtTokenGeneratorTest{

    @Autowired
    @Qualifier("jwtProperties")
    private JwtProperties jwtProperties;

    @Autowired
    private JwtTokenGenerator jwtTokenGenerator;

    @BeforeEach
    void setUp(){
        jwtTokenGenerator = new JwtTokenGenerator(jwtProperties);
    }

    @Test
    void testGenerateToken(){
        String email = "test@google.com";
        Collection<GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_USER")
                , new SimpleGrantedAuthority("ROLE_ADMIN")
        );

        String token = jwtTokenGenerator.generateToken(email, authorities);

        assertNotNull(token);

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals(email, claims.getSubject());
        assertTrue(claims.getExpiration().after(new Date()));
        List<String> roles = claims.get("roles", List.class);
        assertTrue(roles.contains("ROLE_USER"));
        assertTrue(roles.contains("ROLE_ADMIN"));

        long expectedExpiration = claims.getIssuedAt().getTime() + jwtProperties.getAccessExpireTime();
        assertEquals(expectedExpiration, claims.getExpiration().getTime());
    }

    @Test
    void testGenerateRefreshToken(){
        String email = "test@google.com";
        List<String> roles = Arrays.asList("ROLE_USER", "ROLE_ADMIN");

        String refreshToken = jwtTokenGenerator.generateRefreshToken(email, roles);

        assertNotNull(refreshToken);

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtProperties.getRefreshSecretKey().getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(refreshToken)
                .getBody();

        assertEquals(email, claims.getSubject());
        assertTrue(claims.getExpiration().after(new Date()));

        long expectedExpiration = claims.getIssuedAt().getTime() + jwtProperties.getRefreshExpireTime();
        assertEquals(expectedExpiration, claims.getExpiration().getTime());

        assertNull(claims.get("roles"));
    }

}