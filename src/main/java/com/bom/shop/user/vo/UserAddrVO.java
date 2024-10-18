package com.bom.shop.user.vo;

import lombok.Data;

@Data
public class UserAddrVO {
    private int addrCode;
    private String addr;
    private String addrDetail;
    private String postCode;
    private boolean isDefault;
    private String userId;
    private String createdAt;
    private String updatedAt;
}
