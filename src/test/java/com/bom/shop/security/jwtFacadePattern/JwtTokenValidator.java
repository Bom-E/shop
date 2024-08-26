package com.bom.shop.security.jwtFacadePattern;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service("jwtTokenValidator")
public class JwtTokenValidator {

    private final JwtProperties jwtProperties;

    public JwtTokenValidator(JwtProperties jwtProperties){
        this.jwtProperties = jwtProperties;
    }

    private Key getKey(boolean isRefreshToken){
        String keyString = isRefreshToken ? jwtProperties.getRefreshSecretKey() : jwtProperties.getSecretKey();
        return Keys.hmacShaKeyFor(keyString.getBytes(StandardCharsets.UTF_8));
    }

    public boolean isTokenValid(String token, boolean isRefreshToken){
        try{
            Jwts.parserBuilder()
                .setSigningKey(getKey(isRefreshToken))
                .build()
                .parseClaimsJws(token);
            return true;
        }catch (JwtException e){
            return false;
        }
    }

    public boolean isTokenExpired(String token, boolean isRefreshToken){
        try{
            Date expiration = Jwts.parserBuilder()
                    .setSigningKey(getKey(isRefreshToken))
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();
            return expiration.before(new Date());
        }catch (JwtException e){
            return true;
        }
    }
}
