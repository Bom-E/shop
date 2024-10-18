package com.bom.shop.security.jwtFacadePattern;

import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.ssl.SslBundleProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class JwtTokenValidatorTest {

    @Autowired
    @Qualifier("jwtProperties")
    private JwtProperties jwtProperties;

    @Autowired
    private JwtTokenValidator jwtTokenValidator;

    @Autowired
    private JwtTokenGenerator jwtTokenGenerator;

    private Key secretKey;

    @BeforeEach
    void setUp(){
        secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void tokenValidTest(){
        String validToken = jwtTokenGenerator.generateToken("user", Collections.emptyList());

        System.out.println("Valid token: " + validToken);
        System.out.println("Test key: " + Base64.getEncoder().encodeToString(secretKey.getEncoded()));
        System.out.println("JwtProperties key: " + Base64.getEncoder().encodeToString(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8)));

        assertTrue(jwtTokenValidator.isTokenValid(validToken, false), "Valid token should be accepted");

        String invalidToken =  validToken + "invalid";
        assertFalse(jwtTokenValidator.isTokenValid(invalidToken, false), "Invalid token should be rejected");
    }

    @Test
    void isTokenExpiredTest() {
        Date pastDate = new Date(System.currentTimeMillis() - 1000);
        String expiredToken = Jwts.builder()
                .setSubject("user")
                .setIssuedAt(pastDate)
                .setExpiration(pastDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        assertTrue(jwtTokenValidator.isTokenExpired(expiredToken, false), "Expired token should be recognized");

        String validToken = jwtTokenGenerator.generateToken("user", Collections.emptyList());

        assertFalse(jwtTokenValidator.isTokenExpired(validToken, false), "Valid token should not be recognized");
    }

    @Test
    void refreshTokenTest(){
        String validRefreshToken = jwtTokenGenerator.generateRefreshToken("user", Collections.singletonList("ROLE_USER"));
        assertTrue(jwtTokenValidator.isTokenValid(validRefreshToken, true), "Valid refresh token should be accepted");

        String invalidRefreshToken = "invalidRefreshToken";
        assertFalse(jwtTokenValidator.isTokenValid(invalidRefreshToken, true), "Invalid refresh token should be rejected");
    }
}
