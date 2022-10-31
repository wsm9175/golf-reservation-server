package com.lodong.spring.golfreservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Date;
import java.time.LocalDate;


@Data
public class RegistrationDto {
    private String userId;
    private String password;
    private String name;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate birth;
    private String phoneNumber;
    private boolean agreeTerm;
}
