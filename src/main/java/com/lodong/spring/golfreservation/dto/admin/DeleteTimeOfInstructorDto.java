package com.lodong.spring.golfreservation.dto.admin;

import lombok.Data;

import java.time.LocalTime;
import java.util.List;

@Data
public class DeleteTimeOfInstructorDto {
    private String instructorId;
    private List<LocalTime> timeList;
}
