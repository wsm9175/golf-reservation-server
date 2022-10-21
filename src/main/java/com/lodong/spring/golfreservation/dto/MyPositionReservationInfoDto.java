package com.lodong.spring.golfreservation.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class MyPositionReservationInfoDto {
    private String id;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDateTime createAt;
    private int positionId;

}
