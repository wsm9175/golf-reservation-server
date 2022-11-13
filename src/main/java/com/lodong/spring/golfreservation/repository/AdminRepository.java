package com.lodong.spring.golfreservation.repository;

import com.lodong.spring.golfreservation.domain.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository  extends JpaRepository<Admin, String> {
    public Optional<Admin> findByAdminIdAndPassword(String id, String password);
}
