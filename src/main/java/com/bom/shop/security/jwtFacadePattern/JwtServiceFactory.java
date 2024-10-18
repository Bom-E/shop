package com.bom.shop.security.jwtFacadePattern;

import org.springframework.stereotype.Component;

@Component
public class JwtServiceFactory {
    private final JwtTokenGenerator generator;
    private final JwtTokenValidator validator;
    private final JwtTokenParser parser;

    public JwtServiceFactory(JwtTokenGenerator generator
                            , JwtTokenValidator validator
                            , JwtTokenParser parser){
        this.generator = generator;
        this.validator = validator;
        this.parser = parser;
    }

    public JwtService createJwtService(){
        return new JwtServiceImpl(generator, validator, parser);
    }
}
