package com.lodong.spring.golfreservation.repository;

import com.lodong.spring.golfreservation.domain.delete.Position;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionRepository extends JpaRepository<Position, Integer> {
}
