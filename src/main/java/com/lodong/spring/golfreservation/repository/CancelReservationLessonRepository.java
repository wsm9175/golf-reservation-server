package com.lodong.spring.golfreservation.repository;

import com.lodong.spring.golfreservation.domain.User;
import com.lodong.spring.golfreservation.domain.cancel.CancelReservationLesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CancelReservationLessonRepository extends JpaRepository<CancelReservationLesson, String> {

    public List<CancelReservationLesson> findByUserAndCreateAt(User user, LocalDate createAt);
}
