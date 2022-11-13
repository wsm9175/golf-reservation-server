package com.lodong.spring.golfreservation.repository;

import com.lodong.spring.golfreservation.domain.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HolidayRepository extends JpaRepository<Holiday, String> {
}
