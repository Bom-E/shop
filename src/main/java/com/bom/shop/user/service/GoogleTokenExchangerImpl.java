package com.bom.shop.user.service;

import com.bom.shop.utility.OAuth2Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service("GoogleTokenExchangerService")
public class GoogleTokenExchangerImpl implements OAuth2TokenExchangerService{
    private final OAuth2Config oAuth2Config;
    private final RestTemplate restTemplate;

    @Autowired
    public GoogleTokenExchangerImpl(OAuth2Config oauth2Config){
        this.oAuth2Config = oauth2Config;
        this.restTemplate = new RestTemplate();
    }


    @Override
    public String exchangeCodeForToken(String code) {
        String tokenUrl = "https://oauth2.googleapis.com/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", oAuth2Config.getGoogle().getClientId());
        params.add("client_secret", oAuth2Config.getGoogle().getClientSecret());
        params.add("redirect_uri", oAuth2Config.getGoogle().getRedirectUri());
        params.add("grant_type", "authorization_code");

        return executeTokenRequest(tokenUrl, params);

    }

    @Override
    public Map<String, Object> getUserInfo(String accessToken) {
        String userInfoUrl = "https://www.googleapis.com/oauth2/v3/userinfo";
        return executeUserInfoRequest(userInfoUrl, accessToken);
    }

    private String executeTokenRequest(String url, MultiValueMap<String, String> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        if(response.getBody() != null){
            return (String) response.getBody().get("access_token");
        }
        throw new RuntimeException("Failed to get access token");
    }

    private Map<String, Object> executeUserInfoRequest(String url, String accessToken){
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                url
                , HttpMethod.GET
                , entity
                , Map.class
        );

        return response.getBody();
    }
}
