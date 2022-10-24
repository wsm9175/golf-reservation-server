package com.lodong.spring.golfreservation.repository;

import com.lodong.spring.golfreservation.domain.ChangePassword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChangePasswordRepository extends JpaRepository<ChangePassword, String> {

}
