package com.lodong.spring.golfreservation.responseentity.service;

import com.lodong.spring.golfreservation.domain.Advertisement;
import com.lodong.spring.golfreservation.domain.Holiday;
import com.lodong.spring.golfreservation.repository.AdvertisementRepository;
import com.lodong.spring.golfreservation.repository.HolidayRepository;
import com.lodong.spring.golfreservation.util.RequestHoliday;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MainService {
    private final AdvertisementRepository advertisementRepository;
    private final HolidayRepository holidayRepository;
    public List<String> getAdverTisementList(){
        List<Advertisement> advertisementList = advertisementRepository.findAll();
        List<String> advertisementNameList  = new ArrayList<>();
        for(Advertisement advertisement:advertisementList){
            advertisementNameList.add(advertisement.getName());
        }

        return advertisementNameList;
    }

    public void updateHoliday(String year, String month){
        ArrayList<HashMap> responseHolidayArr = new ArrayList<>();
        try {
            Map<String, Object> holidayMap = RequestHoliday.holidayInfoAPI(year, month);
            Map<String, Object> response = (Map<String, Object>) holidayMap.get("response");
            Map<String, Object> body = (Map<String, Object>) response.get("body");
            System.out.println("body = " + body);

            int totalCount = (int) body.get("totalCount");
            if (totalCount <= 0) {
                System.out.println("공휴일 없음");
            }
            if (totalCount == 1) {
                HashMap<String, Object> items = (HashMap<String, Object>) body.get("items");
                HashMap<String, Object> item = (HashMap<String, Object>) items.get("item");
                responseHolidayArr.add(item);
                System.out.println("item = " + item);
            }
            if (totalCount > 1) {
                HashMap<String, Object> items = (HashMap<String, Object>) body.get("items");
                ArrayList<HashMap<String, Object>> item = (ArrayList<HashMap<String, Object>>) items.get("item");
                for (HashMap<String, Object> itemMap : item) {
                    System.out.println("itemMap = " + itemMap);
                    responseHolidayArr.add(itemMap);
                }
            }

            ArrayList<Holiday> holidays = new ArrayList<>();
            for(HashMap map:responseHolidayArr){
                Holiday holiday = Holiday.builder()
                        .id(String.valueOf(map.get("locdate")))
                        .name((String) map.get("dateName"))
                        .build();

                holidays.add(holiday);
            }

            holidayRepository.saveAll(holidays);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
