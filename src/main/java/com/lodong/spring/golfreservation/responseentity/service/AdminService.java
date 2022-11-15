package com.lodong.spring.golfreservation.responseentity.service;


import com.lodong.spring.golfreservation.domain.lesson.Instructor;
import com.lodong.spring.golfreservation.domain.lesson.InstructorTime;
import com.lodong.spring.golfreservation.domain.lesson.LessonTimeTable;
import com.lodong.spring.golfreservation.dto.admin.AddTimeOfInstructorDto;
import com.lodong.spring.golfreservation.repository.InstructorRepository;
import com.lodong.spring.golfreservation.repository.InstructorTimeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {
    private final InstructorTimeRepository instructorTimeRepository;
    private final InstructorRepository instructorRepository;

    public void addInstructorTime(AddTimeOfInstructorDto addTimeOfInstructorDto) {
        List<LocalTime> localTimeList = addTimeOfInstructorDto.getTimeList();

        List<InstructorTime> instructorTimeList = new ArrayList<>();
        for (LocalTime localTime : localTimeList) {
            InstructorTime instructorTime = new InstructorTime();
            LessonTimeTable lessonTimeTable = LessonTimeTable
                    .builder()
                    .startTime(localTime)
                    .endTime(localTime.plusMinutes(15))
                    .build();
            Instructor instructor = instructorRepository
                    .findById(addTimeOfInstructorDto.getInstructorId())
                    .orElseThrow(()->new NullPointerException("없는 강사 입니다."));

            instructorTime.setId(UUID.randomUUID().toString());
            instructorTime.setLessonTimeTable(lessonTimeTable);
            instructorTime.setInstructor(instructor);

            instructorTimeList.add(instructorTime);
        }
        instructorTimeRepository.saveAll(instructorTimeList);
    }



    /*public void deleteInstructorTime(DeleteTimeOfInstructorDto deleteTimeOfInstructorDto){
        String id = deleteTimeOfInstructorDto.getInstructorId();
        List<LocalTime> localTimeList = deleteTimeOfInstructorDto.getTimeList();

        List<InstructorTime> instructorTimeList = new ArrayList<>();
        for (LocalTime localTime : localTimeList) {
            InstructorTime instructorTime = instructorTimeRepository
                    .findByLessonTimeTable_StartTimeAndInstructorId(localTime, id)
                    .orElseThrow(()->new NullPointerException("해당 시간은 없는 시간입니다."));
            instructorTimeList.add(instructorTime);
        }
    }*/
}
