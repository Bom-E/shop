package com.bom.shop.security.jwtFacadePattern;

import com.bom.shop.security.jwtFacadePattern.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("jwtService")
public class JwtServiceImpl implements JwtService {
    private final JwtTokenGenerator generator;
    private final JwtTokenValidator validator;
    private final JwtTokenParser parser;

    public JwtServiceImpl(JwtTokenGenerator jwtTokenGenerator, JwtTokenValidator jwtTokenValidator,
                          JwtTokenParser jwtTokenParser){
        this.generator = jwtTokenGenerator;
        this.validator = jwtTokenValidator;
        this.parser = jwtTokenParser;
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
    public String getEmail(String token, boolean isRefreshToken) {
        return parser.getEmail(token, isRefreshToken);
    }

    @Override
    public List<String> getRoles(String token, boolean isRefreshToken) {
        return parser.getRoles(token, isRefreshToken);
    }

}
