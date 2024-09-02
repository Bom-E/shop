package com.bom.shop.security.jwtFacadePattern;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Service("jwtTokenValidator")
public class JwtTokenValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenValidator.class);
    private final JwtProperties jwtProperties;

    public JwtTokenValidator(JwtProperties jwtProperties){
        this.jwtProperties = jwtProperties;
    }

    private Key getKey(boolean isRefreshToken){
        byte[] keyBytes = isRefreshToken ? jwtProperties.getRefreshSecretKey() : jwtProperties.getSecretKey();
        LOGGER.debug("Using key : {}", Base64.getEncoder().encodeToString(keyBytes));
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(keyBytes));
    }

    public boolean isTokenValid(String token, boolean isRefreshToken){
        try{
            System.out.println("Token: " + token);
            Key key = getKey(isRefreshToken);
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            LOGGER.debug("Token is valid");
            return true;
        }catch (JwtException e){
            LOGGER.debug("Token validation failed: {}", e.getMessage());
            return false;
        }catch (Exception e){
            LOGGER.error("Unexpected error during token validation", e);
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
