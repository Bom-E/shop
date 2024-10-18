package com.bom.shop.security.jwtFacadePattern;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Setter
@Getter
@ToString
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String secretKey;
    private String refreshSecretKey;
    private long accessExpireTime;
    private long refreshExpireTime;

    @PostConstruct
    public void init(){
        System.out.println(this.toString());
    }
}
