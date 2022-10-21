package com.lodong.spring.golfreservation.repository;

import com.lodong.spring.golfreservation.domain.CancelReservation;
import com.lodong.spring.golfreservation.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface CancelReservationRepository extends JpaRepository<CancelReservation, String> {
    public boolean existsByUserAndCreateAt(User user, LocalDate createAt);
}
