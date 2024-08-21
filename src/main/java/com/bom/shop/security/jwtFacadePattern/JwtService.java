package com.bom.shop.security.jwtFacadePattern;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;


public interface JwtService {
    String generateToken(Authentication authentication);
    boolean validateToken(String token, boolean isRefreshToken);
    String getUserEmail(String token, boolean isRefreshToken);
    List<String> getRoles(String token, boolean isRefreshToken);
    Optional<String> resolveToken(HttpServletRequest request);
}
