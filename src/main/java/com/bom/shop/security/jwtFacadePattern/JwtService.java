package com.bom.shop.security.jwtFacadePattern;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;


public interface JwtService {
    String generateToken(Authentication authentication);
    boolean validateToken(String token);
    String getUserEmail(String token);
    List<String> getRoles(String token);
    Optional<String> resolveToken(HttpServletRequest request);
}
