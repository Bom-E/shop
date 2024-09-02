package com.bom.shop.security.jwtFacadePattern;

import com.bom.shop.user.service.UserSignService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtTokenParserTest {

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private JwtTokenValidator jwtTokenValidator;

    @Mock
    private UserSignService userSignService;

    @InjectMocks
    private JwtTokenParser jwtTokenParser;

    private Key key;

    @BeforeEach
    void setUp(){
        String secretKey = "secretKey";
        when(jwtProperties.getSecretKey()).thenReturn("secretKey".getBytes(StandardCharsets.UTF_8));
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecretKey());

    }

    @Test
    void testGetUserEmail(){
        String token = createToken("test@example.com", false);
        when(jwtTokenValidator.isTokenValid(token, false)).thenReturn(true);

        String email = jwtTokenParser.getUserEmail(token, false);
        assertEquals("test@example.com", email);
    }

    @Test
    void testSetGetUsrEmailForRefreshToken(){
        String token = createToken("test@example.com", true);
        when(jwtTokenValidator.isTokenValid(token, true)).thenReturn(true);

        String email = jwtTokenParser.getUserEmail(token, true);
        assertEquals("test@example.com", email);
    }

    @Test
    void testGetRoles(){
        List<String> roles = Arrays.asList("ROLE_USER", "ROLE_ADMIN");
        String token = createTokenWithRoles("test@example.com", roles, false);
        when(jwtTokenValidator.isTokenValid(token, false)).thenReturn(true);

        List<String> retrievedRoles = jwtTokenParser.getRoles(token, false);
        assertEquals(roles, retrievedRoles);
    }

    @Test
    void testGetRolesForRefreshToken(){
        String token = "refreshToken";
        String email = "test@example.com";
        List<String> roles = Arrays.asList("ROLE_USER", "ROLE_ADMIN");

        when(jwtTokenValidator.isTokenValid(token, true)).thenReturn(true);
        when(jwtTokenValidator.getSubject(token, true)).thenReturn(email);

        List<String> retrievedRoles = jwtTokenParser.getRoles(token, true);
        assertEquals(roles, retrievedRoles);
    }

    private String createToken(String subject, boolean isRefreshToken){
        return Jwts.builder()
                .setSubject(subject)
                .signWith(key)
                .compact();
    }

    private String createTokenWithRoles(String subject, List<String> roles, boolean isRefreshToken){
        return Jwts.builder()
                .setSubject(subject)
                .claim("roles", roles)
                .signWith(key)
                .compact();
    }
}
