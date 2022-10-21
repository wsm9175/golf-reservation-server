package com.lodong.spring.golfreservation.dto;

import com.lodong.spring.golfreservation.domain.Instructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class MyLessonReservationInfoDto {
    private String id;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDateTime createAt;
    private int positionId;
    private Instructor instructor;
}
