package com.lodong.spring.golfreservation.repository;

import com.lodong.spring.golfreservation.domain.delete.Position;
import com.lodong.spring.golfreservation.domain.delete.PositionTime;
import com.lodong.spring.golfreservation.domain.delete.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface PositionTimeRepository extends JpaRepository<PositionTime, Integer> {
    public List<PositionTime> findByPosition(Position position);
    public boolean existsByPositionAndTimeId(Position position, Timetable timeId);

    public Optional<List<PositionTime>> findAllByTimeId_StartTimeLessThanOrderByTimeId_StartTime(LocalTime localTime);
    public Optional<List<PositionTime>> findAllByTimeId_StartTimeGreaterThanAndTimeId_EndTimeLessThanOrderByTimeId_StartTime(LocalTime startTime, LocalTime endTime);
}
