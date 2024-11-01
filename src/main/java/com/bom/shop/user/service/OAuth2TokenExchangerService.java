package com.bom.shop.user.service;

import java.util.Map;
import java.util.Objects;

public interface OAuth2TokenExchangerService {

    String exchangeCodeForToken(String code);

    Map<String, Object> getUserInfo(String accessToken);
}
