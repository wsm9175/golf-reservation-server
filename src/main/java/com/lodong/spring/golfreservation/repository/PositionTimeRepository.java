package com.lodong.spring.golfreservation.repository;

import com.lodong.spring.golfreservation.domain.Position;
import com.lodong.spring.golfreservation.domain.PositionTime;
import com.lodong.spring.golfreservation.domain.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Time;
import java.util.List;

public interface PositionTimeRepository extends JpaRepository<PositionTime, Integer> {
    public List<PositionTime> findByPosition(Position position);

    public boolean existsByPositionAndTimeId(Position position, Timetable timeId);
}
