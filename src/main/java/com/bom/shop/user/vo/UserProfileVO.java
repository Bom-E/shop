package com.bom.shop.user.vo;

import lombok.Data;

@Data
public class UserProfileVO {
    private String userId;
    private String userName;
    private String gender;
    private String email;
    private String userTel;
    private String birthDate;
    private String joinDate;
    private String createdAt;
    private String updatedAt;
}
