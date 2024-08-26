package com.bom.shop.security.jwtFacadePattern;

import lombok.Getter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

@Getter
@ToString(exclude = "secretKey")
@Service("jwtProperties")
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secretKey;
    private String refreshSecretKey;
    private long accessExpireTime;
    private long refreshExpireTime;

    public void setSecretKey(String secretKey){
        this.secretKey = secretKey;
    }

    public void setRefreshSecretKey(String refreshSecretKey){
        this.refreshSecretKey = refreshSecretKey;
    }

    public void setAccessExpireTime(long accessExpireTime){
        this.accessExpireTime = accessExpireTime;
    }

    public void setRefreshExpireTime(long refreshExpireTime){
        this.refreshExpireTime = refreshExpireTime;
    }
}
