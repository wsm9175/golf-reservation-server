package com.lodong.spring.golfreservation.repository;

import com.lodong.spring.golfreservation.domain.PositionReservation;
import com.lodong.spring.golfreservation.domain.Timetable;
import com.lodong.spring.golfreservation.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public interface PositionReservationRepository extends JpaRepository<PositionReservation, String> {
    /*public List<PositionReservation> findByDateAndPositionId(LocalDate date, int positionId);*/
    public List<PositionReservation> findByDate(LocalDate date);

    public List<PositionReservation> findByUserId(User user);
}
