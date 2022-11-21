package com.lodong.spring.golfreservation.repository;

import com.lodong.spring.golfreservation.domain.lesson.InstructorTime;
import com.lodong.spring.golfreservation.domain.lesson.LessonLock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface LessonLockRespository extends JpaRepository<LessonLock, String> {
    public Optional<List<LessonLock>> findByDateAndInstructorTime_Instructor_Id(LocalDate date, String instructorId);
    public Optional<LessonLock> findByDateAndInstructorTime_LessonTimeTable_StartTimeAndInstructorTime_Instructor_Id(LocalDate date, LocalTime time, String instructorId);

    public void deleteByDateAndInstructorTime(LocalDate date, InstructorTime instructorTime);
}
