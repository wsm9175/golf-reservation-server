package com.lodong.spring.golfreservation.repository;

import com.lodong.spring.golfreservation.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findById(String id);
    Optional<User> findByUserId(String id);
    boolean existsByUserId(String id);
    boolean existsByPhoneNumber(String phoneNumber);
    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findByPhoneNumberAndUserId(String phoneNumber, String userId);


    @Modifying
    @Query(value = "UPDATE User u set u.password = :password WHERE u.userId = :userId")
    void updateUser(String password, String userId);

}
