package com.bom.shop.security.jwtFacadePattern;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    @Autowired
    public JwtTokenValidator(@Qualifier("jwtProperties") JwtProperties jwtProperties){
        this.jwtProperties = jwtProperties;
    }

    private Key getKey(boolean isRefreshToken){
        String keyString = isRefreshToken ? jwtProperties.getRefreshSecretKey() : jwtProperties.getSecretKey();
        LOGGER.debug("Using key : {}", keyString);
        return Keys.hmacShaKeyFor(keyString.getBytes(StandardCharsets.UTF_8));
    }

    public boolean isTokenValid(String token, boolean isRefreshToken){
        try{
            LOGGER.debug("Validating token: {}", token);
            Key key = getKey(isRefreshToken);
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            LOGGER.debug("Token is valid");
            return true;
        }catch (JwtException e){
            LOGGER.debug("Token validation failed: {}", e.getMessage());
        }catch (Exception e){
            LOGGER.error("Unexpected error during token validation", e);
        }
        return false;
    }

    public boolean isTokenExpired(String token, boolean isRefreshToken){
        try{
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getKey(isRefreshToken))
                    .setAllowedClockSkewSeconds(60)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Date expiration= claims.getExpiration();
            boolean isExpired = expiration.before(new Date());

            if(isExpired){
                LOGGER.debug("Token is expired");
            } else {
                LOGGER.debug("Token is not expired");
            }
            return isExpired;
        }catch (ExpiredJwtException e){
            LOGGER.debug("Token is already expired", e);
            return true;
        }catch (JwtException e) {
            LOGGER.error("Invalid JWT token", e);
            throw new io.jsonwebtoken.JwtException("Invalid JWT token", e);
        }
    }


}
