package com.bom.shop.security.jwtFacadePattern;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

public interface JwtService {

    String generateToken(Authentication authentication);
    boolean validateToken(String token, boolean isRefreshToken);
    String getEmail(String token, boolean isRefreshToken);
    List<String> getRoles(String token, boolean isRefreshToken);

}
