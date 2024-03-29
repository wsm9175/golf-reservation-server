package com.lodong.spring.golfreservation.repository;

import com.lodong.spring.golfreservation.domain.delete.PositionReservation;
import com.lodong.spring.golfreservation.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface PositionReservationRepository extends JpaRepository<PositionReservation, String> {
    /*public List<PositionReservation> findByDateAndPositionId(LocalDate date, int positionId);*/
    public Optional<List<PositionReservation>> findByDate(LocalDate date);
    public List<PositionReservation> findByUserId(User user);
    public boolean existsByUserIdAndDate(User user, LocalDate date);
    public void deleteByDateAndTime_StartTimeAndPositionId(LocalDate date, LocalTime startTime, int positionId);
}
