package com.lodong.spring.golfreservation.repository;

import com.lodong.spring.golfreservation.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findById(String id);
    Optional<User> findByUserId(String id);
    boolean existsByUserId(String id);
    boolean existsByPhoneNumber(String phoneNumber);
}
