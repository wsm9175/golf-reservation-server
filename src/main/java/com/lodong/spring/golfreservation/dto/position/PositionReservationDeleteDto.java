package com.lodong.spring.golfreservation.dto.position;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class PositionReservationDeleteDto {
    private LocalDate date;
    private LocalTime time;
    private int positionId;
}
