package com.lodong.spring.golfreservation.dto;

import lombok.Data;

import java.sql.Date;

@Data
public class UserDto {
    private String userId;
    private String name;
    private Date birth;
    private String phoneNumber;
}
