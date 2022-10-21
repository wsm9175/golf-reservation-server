package com.lodong.spring.golfreservation.repository;


import com.lodong.spring.golfreservation.domain.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdvertisementRepository extends JpaRepository<Advertisement, String> {

}
