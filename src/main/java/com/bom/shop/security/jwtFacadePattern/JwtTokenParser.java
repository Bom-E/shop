package com.bom.shop.security.jwtFacadePattern;

import com.bom.shop.user.vo.UserProfileVO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;

@Service("jwtTokenParser")
public class JwtTokenParser {
    private final JwtProperties jwtProperties;
    private final JwtTokenValidator jwtTokenValidator;

    @Autowired
    public JwtTokenParser(@Qualifier("jwtProperties") JwtProperties jwtProperties, JwtTokenValidator jwtTokenValidator){
        this.jwtProperties = jwtProperties;
        this.jwtTokenValidator = jwtTokenValidator;
    }

    private Key getKey(boolean isRefreshToken){
        String keyString = isRefreshToken ? jwtProperties.getRefreshSecretKey() : jwtProperties.getSecretKey();
        return Keys.hmacShaKeyFor(keyString.getBytes(StandardCharsets.UTF_8));
    }

    public String getEmail(String token, boolean isRefreshToken){
        if(!jwtTokenValidator.isTokenValid(token, isRefreshToken)){
            throw new JwtException("Invalid token");
        }
        Claims claims = getClaims(token, isRefreshToken);
        System.out.println("Extracted subject: " + claims.getSubject());
        return claims.getSubject();
    }

    public List<String> getRoles(String token, boolean isRefreshToken){
        if(!jwtTokenValidator.isTokenValid(token, isRefreshToken)){
            throw new JwtException("Invalid token");
        }

        Claims claims = getClaims(token, isRefreshToken);
        UserProfileVO userProfileVO = new UserProfileVO();
        userProfileVO.setEmail(claims.getSubject());

        if(!isRefreshToken && claims.get("roles") != null){
            return claims.get("roles", List.class);
        } else {
            return List.of("ROLE_USER");
        }
    }

    private Claims getClaims(String token, boolean isRefreshToken){
        Key key = isRefreshToken ?
                Keys.hmacShaKeyFor(jwtProperties.getRefreshSecretKey().getBytes(StandardCharsets.UTF_8)) :
                Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        System.out.println("Parsed claims: " + claims);
        return claims;
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
