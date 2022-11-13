package com.lodong.spring.golfreservation.repository;

import com.lodong.spring.golfreservation.domain.Position;
import com.lodong.spring.golfreservation.domain.PositionTime;
import com.lodong.spring.golfreservation.domain.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface PositionTimeRepository extends JpaRepository<PositionTime, Integer> {
    public List<PositionTime> findByPosition(Position position);
    public boolean existsByPositionAndTimeId(Position position, Timetable timeId);

    public Optional<List<PositionTime>> findAllByTimeId_StartTimeLessThanOrderByTimeId_StartTime(LocalTime localTime);
    public Optional<List<PositionTime>> findAllByTimeId_StartTimeGreaterThanAndTimeId_EndTimeLessThanOrderByTimeId_StartTime(LocalTime startTime, LocalTime endTime);
}
