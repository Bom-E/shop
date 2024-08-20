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

    public JwtServiceImpl(JwtTokenGenerator generator, JwtTokenValidator validator,
                          JwtTokenParser parser, JwtTokenResolver resolver){
        this.generator = generator;
        this.validator = validator;
        this.parser = parser;
        this.resolver = resolver;
    }

    @Override
    public String generateToken(Authentication authentication) {
        return generator.generateToken(authentication.getName(), authentication.getAuthorities());
    }

    @Override
    public boolean validateToken(String token) {
        return validator.validateToken(token);
    }

    @Override
    public String getUserEmail(String token) {
        return parser.getUserEmail(token);
    }

    @Override
    public List<String> getRoles(String token) {
        return parser.getRoles(token);
    }

    @Override
    public Optional<String> resolveToken(HttpServletRequest request) {
        return resolver.resolveToken(request);
    }
}
