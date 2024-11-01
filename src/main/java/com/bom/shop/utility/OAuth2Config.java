package com.bom.shop.utility;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "oauth2")
public class OAuth2Config {
    private GoogleProperties google;
    private NaverProperties naver;
    private KakaoProperties kakao;


    @Setter
    @Getter
    public static class GoogleProperties{
        private String clientId;
        private String clientSecret;
        private String redirectUri;
    }

    @Setter
    @Getter
    public static class NaverProperties{
        private String clientId;
        private String clientSecret;
        private String redirectUri;
    }

    @Setter
    @Getter
    public static class KakaoProperties{
        private String clientId;
        private String clientSecret;
        private String redirectUri;
    }
}
