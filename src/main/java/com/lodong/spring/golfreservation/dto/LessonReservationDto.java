package com.lodong.spring.golfreservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class LessonReservationDto {
    private LocalDate date;
    private LocalTime time;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createAt;
    private int positionId;
    private String userId;
    private String instructorId;
}