package com.lodong.spring.golfreservation.repository;

import com.lodong.spring.golfreservation.domain.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;

public interface TimetableRepository extends JpaRepository<Timetable, String> {
    public Timetable findByStartTime(LocalTime time);
}
