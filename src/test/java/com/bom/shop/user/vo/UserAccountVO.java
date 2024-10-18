package com.bom.shop.user.vo;

import lombok.Data;

@Data
public class UserAccountVO {
    private String userId;
    private String userPw;
    private String userRole;
    private String createdAt;
    private String updatedAt;
    private UserProfileVO userProfileVO;
    private UserAddrVO userAddrVO;
}
