package com.lodong.spring.golfreservation.repository;


import com.lodong.spring.golfreservation.domain.lesson.InstructorTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface InstructorTimeRepository extends JpaRepository<InstructorTime, String> {
    public Optional<List<InstructorTime>> findAllByInstructorIdAndLessonTimeTable_StartTimeLessThanOrderByLessonTimeTable_StartTime(String instructorId, LocalTime time);
    public Optional<List<InstructorTime>> findAllByInstructorIdAndLessonTimeTable_StartTimeGreaterThanAndLessonTimeTable_EndTimeLessThanOrderByLessonTimeTable_StartTime(String instructorId, LocalTime startTime, LocalTime endTime);

    public Optional<InstructorTime> findByLessonTimeTable_StartTime(LocalTime startTime);
    public Optional<InstructorTime> findByLessonTimeTable_StartTimeAndInstructorId(LocalTime startTime, String instructorId);

    public Optional<List<InstructorTime>> findByInstructorIdOrderByLessonTimeTable_StartTime(String instructorId);


}


