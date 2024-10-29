package com.bom.shop.security;

import java.security.SecureRandom;
import java.util.Base64;

public class SecureRandomStringGenerator {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static String generateRandomString(int byteLength) {
        byte[] randomBytes = new byte[byteLength];
        SECURE_RANDOM.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    public static void main(String[] args) {
        // 32바이트(256비트) 길이의 무작위 문자열 생성
        String secretKey = generateRandomString(32);
        System.out.println("생성된 시크릿 키: " + secretKey);
        System.out.println("시크릿 키 길이: " + secretKey.length() + " 문자");

        // 64바이트(512비트) 길이의 무작위 문자열 생성
        String longerSecretKey = generateRandomString(64);
        System.out.println("생성된 긴 시크릿 키: " + longerSecretKey);
        System.out.println("긴 시크릿 키 길이: " + longerSecretKey.length() + " 문자");
    }
}


