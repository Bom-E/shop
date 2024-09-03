package com.bom.shop.security.jwtFacadePattern;

import com.bom.shop.user.service.UserSignService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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
import org.springframework.test.util.ReflectionTestUtils;

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
    private SqlSessionTemplate sqlSession;

    private JwtTokenParser jwtTokenParser;

    @BeforeEach
    void setUp(){
        Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        Key refreshKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

        byte[] secretKeyBytes = secretKey.getEncoded();
        byte[] refreshKeyBytes = refreshKey.getEncoded();

        when(jwtProperties.getSecretKey()).thenReturn(secretKeyBytes);
        when(jwtProperties.getRefreshSecretKey()).thenReturn(refreshKeyBytes);

        jwtTokenParser = new JwtTokenParser(jwtProperties, jwtTokenValidator);

        ReflectionTestUtils.setField(jwtTokenParser, "sqlSession", sqlSession);

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
        String token = createToken("test@example.com", true);
        List<String> roles = Arrays.asList("ROLE_USER", "ROLE_ADMIN");

        when(jwtTokenValidator.isTokenValid(token, true)).thenReturn(true);
        doReturn(roles).when(sqlSession).selectList(eq("userMapper.getRolesByEmail"), eq("test@example.com"));

        List<String> retrievedRoles = jwtTokenParser.getRoles(token, true);
        assertEquals(roles, retrievedRoles);
    }

    @Test
    void testGetTokenClaims(){
        String token = createToken("test@example.com", false);
        when(jwtTokenValidator.isTokenValid(token, false)).thenReturn(true);

        Claims claims = jwtTokenParser.getTokenClaims(token);
        assertEquals("test@example.com", claims.getSubject());
    }

    @Test
    void testGetRefreshTokenClaims(){
        String token = createToken("test@example.com", true);
        when(jwtTokenValidator.isTokenValid(token, true)).thenReturn(true);

        Claims claims = jwtTokenParser.getRefreshTokenClaims(token);
        assertEquals("test@example.com", claims.getSubject());
    }

    @Test
    void testTokenValid(){
        String token = "validToken";
        when(jwtTokenValidator.isTokenValid(token, false)).thenReturn(true);

        assertTrue(jwtTokenParser.isTokenValid(token,false));
    }

    @Test
    void testTokenExpired(){
        String token = "expiredToken";
        when(jwtTokenValidator.isTokenExpired(token, false)).thenReturn(true);

        assertTrue(jwtTokenParser.isTokenExpired(token, false));
    }

    @Test
    void testInvalidToken(){
        String token = "invalidToken";
        when(jwtTokenValidator.isTokenValid(token, false)).thenReturn(false);

        assertThrows(JwtException.class, () -> jwtTokenParser.getUserEmail(token, false));
    }

    private Key getKey(boolean isRefreshToken){
        byte[] keyBytes = isRefreshToken
                ? jwtProperties.getRefreshSecretKey()
                : jwtProperties.getSecretKey();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String createToken(String subject, boolean isRefreshToken){
        return Jwts.builder()
                .setSubject(subject)
                .signWith(getKey(isRefreshToken))
                .compact();
    }

    private String createTokenWithRoles(String subject, List<String> roles, boolean isRefreshToken){
        return Jwts.builder()
                .setSubject(subject)
                .claim("roles", roles)
                .signWith(getKey(isRefreshToken))
                .compact();
    }
}
