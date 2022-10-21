package com.lodong.spring.golfreservation.dto;

import lombok.Data;

import java.sql.Time;
import java.time.LocalTime;

@Data
public class PositionReservationDto {
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean isReservation;
}
