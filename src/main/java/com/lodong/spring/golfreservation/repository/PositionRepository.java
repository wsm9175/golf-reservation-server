package com.lodong.spring.golfreservation.repository;

import com.lodong.spring.golfreservation.domain.Position;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PositionRepository extends JpaRepository<Position, Integer> {
}
