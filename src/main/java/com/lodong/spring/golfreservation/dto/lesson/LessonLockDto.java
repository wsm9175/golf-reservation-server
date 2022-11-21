package com.lodong.spring.golfreservation.dto.lesson;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class LessonLockDto {
    private String instructorId;
    private LocalDate date;
    private LocalTime time;
}
