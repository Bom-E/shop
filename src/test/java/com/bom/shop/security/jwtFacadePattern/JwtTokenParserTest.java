package com.bom.shop.security.jwtFacadePattern;

import com.bom.shop.user.service.UserAuthService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
public class JwtTokenParserTest {

    @Autowired
    @Qualifier("jwtProperties")
    private JwtProperties jwtProperties;

    @Autowired
    private JwtTokenValidator jwtTokenValidator;

    @Autowired
    private UserAuthService userAuthService;

    @Autowired
    private JwtTokenParser jwtTokenParser;

    @Test
    void testGetEmail(){

        String expectedEmail = "test@example.com";
        String token = createToken(expectedEmail, false);

        System.out.println("Generated Token: " + token);

        // 토큰 유효성 검사
        boolean isValid = jwtTokenValidator.isTokenValid(token, false);
        System.out.println("Is Token Valid: " + isValid);

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();
        System.out.println("Parsed claims in test: " + claims);
        System.out.println("Subject from parsed claims: " + claims.getSubject());

        if(!isValid){
            try {
                Jwts.parser().setSigningKey(jwtProperties.getSecretKey().getBytes()).parseClaimsJws(token);
            } catch (Exception e) {
                System.out.println("Token validation error: " + e.getMessage());
            }
        }

        assertTrue(isValid, "Token should be valid");

        // 이메일 추출 및 검증
        String extractedEmail = jwtTokenParser.getEmail(token, false);
        Assertions.assertEquals(expectedEmail, extractedEmail, "Extracted email should match the expected email");

        // 틀린 토큰
        String invalidToken = "invalidToken";
        assertThrows(JwtException.class, () -> jwtTokenParser.getEmail(invalidToken, false),
                "Should throw JwtException for invalid token");
    }

    @Test
    void testSetGetUsrEmailForRefreshToken(){
        String token = createToken("test@example.com", true);
        String email = jwtTokenParser.getEmail(token, true);
        assertEquals("test@example.com", email);
    }

    @Test
    void testGetRoles(){
        List<String> roles = Arrays.asList("ROLE_USER", "ROLE_ADMIN");
        String token = createTokenWithRoles("test@example.com", roles, false);
        List<String> retrievedRoles = jwtTokenParser.getRoles(token, false);
        assertEquals(roles, retrievedRoles);
    }

    @Test
    void testGetRolesForRefreshToken(){
        List<String> expectedRoles = Arrays.asList("ROLE_USER", "ROLE_ADMIN");
        String token = createTokenWithRoles("test@example.com", expectedRoles, true);

        List<String> retrievedRoles = jwtTokenParser.getRoles(token, true);

        assertEquals(expectedRoles, retrievedRoles);

        // 메소드 노서치. 나중에 실 데이터 들어 왔을 때 재 테스트.
    }

    @Test
    void testGetTokenClaims(){
        String token = createToken("test@example.com", false);
        Claims claims = jwtTokenParser.getTokenClaims(token);
        assertEquals("test@example.com", claims.getSubject());
    }

    @Test
    void testGetRefreshTokenClaims(){
        String token = createToken("test@example.com", true);
        Claims claims = jwtTokenParser.getRefreshTokenClaims(token);
        assertEquals("test@example.com", claims.getSubject());
    }

    @Test
    void testTokenValid(){
        String token = createToken("test@example.com", false);

        assertTrue(jwtTokenParser.isTokenValid(token,false));
    }

    @Test
    void testTokenExpired(){
        // 만료 된 토큰 생성
        String expiredToken = Jwts.builder()
                        .setSubject("testUser")
                        .setIssuedAt(new Date(System.currentTimeMillis() - 20000))
                        .setExpiration(new Date(System.currentTimeMillis() - 10000))
                        .signWith(Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8)))
                        .compact();

        // 메소드 호출
        boolean isExpired = jwtTokenParser.isTokenExpired(expiredToken, false);

        assertTrue(isExpired, "Token should be expired");

        String validToken = Jwts.builder()
                .setSubject("testUser")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 10000))
                .signWith(Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8)))
                .compact();

        boolean isValid = !jwtTokenParser.isTokenExpired(validToken, false);

        assertTrue(isValid, "Token should be valid");
    }

    @Test
    void testInvalidToken(){
        String token = "invalidToken";

        assertThrows(JwtException.class, () -> jwtTokenParser.getEmail(token, false));
    }

    private Key getKey(boolean isRefreshToken){
        byte[] keyBytes = isRefreshToken
                ? jwtProperties.getRefreshSecretKey().getBytes(StandardCharsets.UTF_8)
                : jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String createToken(String email, boolean isRefreshToken){
        Key key = isRefreshToken ?
                Keys.hmacShaKeyFor(jwtProperties.getRefreshSecretKey().getBytes(StandardCharsets.UTF_8)) :
                Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setSubject(email)
                .signWith(key)
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
