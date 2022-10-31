package com.lodong.spring.golfreservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalTime;

@Data
public class PositionReservationDto {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss", timezone = "Asia/Seoul")
    private LocalTime startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss", timezone = "Asia/Seoul")
    private LocalTime endTime;
    private String reservationMemberName;
    private boolean isReservation;
}
