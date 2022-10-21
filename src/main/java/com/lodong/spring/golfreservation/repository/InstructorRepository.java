package com.lodong.spring.golfreservation.repository;

import com.lodong.spring.golfreservation.domain.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstructorRepository extends JpaRepository<Instructor, String> {
}
