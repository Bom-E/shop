package com.bom.shop.security.jwtFacadePattern;

import org.springframework.security.core.Authentication;

import java.util.List;


public interface JwtService {
    String generateToken(Authentication authentication);
    boolean validateToken(String token, boolean isRefreshToken);
    String getUserEmail(String token, boolean isRefreshToken);
    List<String> getRoles(String token, boolean isRefreshToken);
}
