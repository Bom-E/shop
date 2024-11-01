package com.bom.shop.utility;

import com.bom.shop.user.service.GoogleTokenExchangerImpl;
import com.bom.shop.user.service.OAuth2TokenExchangerService;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class OAuth2TokenExchangerFactory {
    private final Map<String, OAuth2TokenExchangerService> exchangers;

    public OAuth2TokenExchangerFactory(
            GoogleTokenExchangerImpl googleTokenExchanger
            // , NaverTokenExchangerImpl naverTokenExchanger
            // , KakaoTokenExchangerImpl kakaoTokenExchanger
    ){
        Map<String, OAuth2TokenExchangerService> map = new HashMap<>();
        map.put(
                "google", googleTokenExchanger);
        // map.put("naver", naverTokenExchanger);
        // map.put("kakao", kakaoTokenExchanger);
        this.exchangers = Collections.unmodifiableMap(map);
    }

    public OAuth2TokenExchangerService getExchanger(String provider){
        OAuth2TokenExchangerService exchanger = exchangers.get(provider.toLowerCase());
        if(exchanger == null){
            throw new IllegalArgumentException("지원하지 않는 OAuth 제공자입니다: " + provider);
        }
        return exchanger;
    }

}
