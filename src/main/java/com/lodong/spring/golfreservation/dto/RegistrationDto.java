package com.lodong.spring.golfreservation.dto;

import lombok.Data;

import java.sql.Date;


@Data
public class RegistrationDto {
    private String userId;
    private String password;
    private String name;
    private Date birth;
    private String phoneNumber;
    private boolean agreeTerm;
}
