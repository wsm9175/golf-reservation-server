package com.lodong.spring.golfreservation.repository;

import com.lodong.spring.golfreservation.domain.PositionLock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;

public interface PositionLockRepository extends JpaRepository<PositionLock, String> {
    public void deleteByPositionIdAndDateAndTimetableStartTime(int positionId, LocalDate date, LocalTime startTime);
}
