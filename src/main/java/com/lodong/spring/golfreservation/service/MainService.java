package com.lodong.spring.golfreservation.service;

import com.lodong.spring.golfreservation.domain.Advertisement;
import com.lodong.spring.golfreservation.repository.AdvertisementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MainService {
    private final AdvertisementRepository advertisementRepository;

    public List<String> getAdverTisementList(){
        List<Advertisement> advertisementList = advertisementRepository.findAll();
        List<String> advertisementNameList  = new ArrayList<>();
        for(Advertisement advertisement:advertisementList){
            advertisementNameList.add(advertisement.getName());
        }

        return advertisementNameList;
    }

}
