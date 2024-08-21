package com.bom.shop.security.jwtFacadePattern;

import com.bom.shop.security.jwtFacadePattern.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

public class JwtServiceImpl implements JwtService {
    private final JwtTokenGenerator generator;
    private final JwtTokenValidator validator;
    private final JwtTokenParser parser;
    private final JwtTokenResolver resolver;

    public JwtServiceImpl(JwtTokenGenerator jwtTokenGenerator, JwtTokenValidator jwtTokenValidator,
                          JwtTokenParser jwtTokenParser, JwtTokenResolver jwtTokenResolver){
        this.generator = jwtTokenGenerator;
        this.validator = jwtTokenValidator;
        this.parser = jwtTokenParser;
        this.resolver = jwtTokenResolver;
    }

    @Override
    public String generateToken(Authentication authentication) {
        return generator.generateToken(authentication.getName(), authentication.getAuthorities());
    }

    @Override
    public boolean validateToken(String token, boolean isRefreshToken) {
        return validator.isTokenValid(token, isRefreshToken);
    }

    @Override
    public String getUserEmail(String token, boolean isRefreshToken) {
        return parser.getUserEmail(token, isRefreshToken);
    }

    @Override
    public List<String> getRoles(String token, boolean isRefreshToken) {
        return parser.getRoles(token, isRefreshToken);
    }

    @Override
    public Optional<String> resolveToken(HttpServletRequest request) {
        return resolver.resolveToken(request);
    }
}
