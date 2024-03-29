package com.lodong.spring.golfreservation.repository;

import com.lodong.spring.golfreservation.domain.lesson.LessonReservation;
import com.lodong.spring.golfreservation.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface LessonReservationRepository extends JpaRepository<LessonReservation, String> {
    public List<LessonReservation> findByUser(User user);

    public Optional<List<LessonReservation>> findByDateAndInstructorId(LocalDate date, String instructorId);

    public boolean existsByUserAndDate(User user, LocalDate date);

    public void deleteByDateAndTime_LessonTimeTable_StartTimeAndInstructor_Id(LocalDate date, LocalTime startTime, String instructorId);

    public Optional<List<LessonReservation>> findByDate(LocalDate date);
}
