package com.bom.shop.security.jwtFacadePattern;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;
import java.util.stream.Collectors;

@Service("jwtTokenParser")
public class JwtTokenParser {
    private final Key key;
    private final Key refreshKey;
    private final JwtTokenValidator jwtTokenValidator;

    @Autowired
    private SqlSessionTemplate sqlSession;

    public JwtTokenParser(JwtProperties jwtProperties, JwtTokenValidator jwtTokenValidator){
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecretKey());
        this.refreshKey = Keys.hmacShaKeyFor(jwtProperties.getRefreshSecretKey());
        this.jwtTokenValidator = jwtTokenValidator;
    }

    private Key getKey(boolean isRefreshToken){
        return isRefreshToken ? refreshKey : key;
    }

    public String getUserEmail(String token, boolean isRefreshToken){
        if(!jwtTokenValidator.isTokenValid(token, isRefreshToken)){
            throw new JwtException("Invalid token");
        }
        Claims claims = getClaims(token, isRefreshToken);
        return claims.getSubject();
    }

    public List<String> getRoles(String token, boolean isRefreshToken){
        if(!jwtTokenValidator.isTokenValid(token, isRefreshToken)){
            throw new JwtException("Invalid token");
        }
        Claims claims = getClaims(token, isRefreshToken);
        if(!isRefreshToken && claims.get("roles") != null){
            return claims.get("roles", List.class);
        }else {
            String userEmail = claims.getSubject();
            return sqlSession.selectList("userMapper.getRolesByEmail", userEmail);
        }
    }

    private Claims getClaims(String token, boolean isRefreshToken){
        return Jwts.parserBuilder()
                .setSigningKey(getKey(isRefreshToken))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Claims getTokenClaims(String token){
        if(!jwtTokenValidator.isTokenValid(token, false)){
            throw new JwtException("Invalid token");
        }
        return getClaims(token, false);
    }

    public Claims getRefreshTokenClaims(String token){
        if(!jwtTokenValidator.isTokenValid(token, true)){
            throw new JwtException("Invalid refresh token");
        }
        return getClaims(token, true);
    }

    public boolean isTokenValid(String token, boolean isRefreshToken){
        return jwtTokenValidator.isTokenValid(token, isRefreshToken);
    }

    public boolean isTokenExpired(String token, boolean isRefreshToken){
        return jwtTokenValidator.isTokenExpired(token, isRefreshToken);
    }
}
