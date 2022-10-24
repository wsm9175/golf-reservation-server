package com.lodong.spring.golfreservation.dto;

import lombok.Data;

@Data
public class ChangePasswordDto {
    private String changeUUID;
    private String changePassword;
    private String userId;
    private String phoneNumber;
}
