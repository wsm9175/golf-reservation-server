package com.lodong.spring.golfreservation.dto.lesson;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class LessonReservationDeleteDto {
    private LocalDate date;
    private LocalTime time;
    private String instructorId;

}
