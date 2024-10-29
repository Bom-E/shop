package com.bom.shop.user.vo;

import lombok.Data;

@Data
public class RefreshTokenManageVO {

    private long id;
    private String userId;
    private String refreshToken;
    private String issuedAt;
    private String expiresAt;
}
