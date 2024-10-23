package com.bom.shop.utility;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {
    @Value("${jwt.access-expire-time}")
    private long accessExpiredTime;

    @Value("${jwt.refresh-expire-time}")
    private long refreshExpireTime;

    // 쿠키 이름 상수
    public static final String ACCESS_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";

    // 쿠키 설정 관련 상수
    private static final String COOKIE_PATH = "/";

    private int convertToSeconds(long milliseconds){
        return (int) (milliseconds / 1000);
    }

    public void addAccessTokenCookie(HttpServletResponse response, String value){
        Cookie cookie = new Cookie(ACCESS_TOKEN, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath(COOKIE_PATH);
        cookie.setMaxAge(convertToSeconds(accessExpiredTime));
        response.addCookie(cookie);
    }

    public void addRefreshTokenCookie(HttpServletResponse response, String value){
        Cookie cookie = new Cookie(REFRESH_TOKEN, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath(COOKIE_PATH);
        cookie.setMaxAge(convertToSeconds(refreshExpireTime));
        response.addCookie(cookie);
    }

    public void addAuthTokenCookie(HttpServletResponse response, String accessToken, String refreshToken){
        addAccessTokenCookie(response, accessToken);
        addRefreshTokenCookie(response, refreshToken);
    }

    public String extractTokenFromCookies(Cookie[] cookies, String tokenType){
        if(cookies != null){
            for(Cookie cookie : cookies){
                if(tokenType.equals(cookie.getName())){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
