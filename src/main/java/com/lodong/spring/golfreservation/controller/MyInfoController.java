package com.lodong.spring.golfreservation.controller;

import com.lodong.spring.golfreservation.domain.User;
import com.lodong.spring.golfreservation.dto.CancelReservationDto;
import com.lodong.spring.golfreservation.dto.UserDto;
import com.lodong.spring.golfreservation.responseentity.StatusEnum;
import com.lodong.spring.golfreservation.responseentity.service.MyInfoService;
import com.lodong.spring.golfreservation.util.MakeResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("rest/v1/my")
public class MyInfoController {
    private final MyInfoService myInfoService;

    public MyInfoController(MyInfoService myInfoService) {
        this.myInfoService = myInfoService;
    }

    @GetMapping("/info")
    public ResponseEntity<?> getMyInfo(String uid) {
        try {
            User user = myInfoService.getMyInfo(uid);
            UserDto userDto = new UserDto();
            userDto.setUserId(user.getUserId());
            userDto.setBirth(user.getBirth());
            userDto.setPhoneNumber(user.getPhoneNumber());
            userDto.setName(user.getName());
            StatusEnum statusEnum = StatusEnum.OK;
            String message = "유저정보";
            return MakeResponseEntity.getResponseMessage(statusEnum, message, userDto);
        } catch (NullPointerException nullPointerException) {
            StatusEnum statusEnum = StatusEnum.NOT_FOUND;
            String message = nullPointerException.getMessage();
            return MakeResponseEntity.getResponseMessage(statusEnum, message);
        }
    }

    @GetMapping("/reservation-list")
    public ResponseEntity<?> getMyReservationList(String uid) {
        try {
            HashMap<String, List<?>> myReservationInfo = myInfoService.getMyReservationList(uid);
            StatusEnum statusEnum = StatusEnum.OK;
            String message = "나의 예약 정보";
            return MakeResponseEntity.getResponseMessage(statusEnum, message, myReservationInfo);
        } catch (NullPointerException nullPointerException) {
            StatusEnum statusEnum = StatusEnum.NOT_FOUND;
            String message = nullPointerException.getMessage();
            return MakeResponseEntity.getResponseMessage(statusEnum, message);
        }
    }
    @DeleteMapping("/cancel/reservation")
    public ResponseEntity<?> cancelReservation(@RequestBody CancelReservationDto cancelReservation) {
        try {
            myInfoService.cancelReservation(cancelReservation);
            StatusEnum statusEnum = StatusEnum.OK;
            String message = "예약 취소 완료";
            return MakeResponseEntity.getResponseMessage(statusEnum, message);
        }catch (RuntimeException runtimeException){
            StatusEnum statusEnum = StatusEnum.BAD_REQUEST;
            String message = runtimeException.getMessage();
            return MakeResponseEntity.getResponseMessage(statusEnum, message);
        }
    }
    @GetMapping("/today-reservation")
    public ResponseEntity<?> isReservationToday(String uid,@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            boolean isTodayReservation = myInfoService.isReservationToday(uid, date);
            StatusEnum statusEnum = StatusEnum.OK;
            String message = "오늘 예약 했는지 여부";
            return MakeResponseEntity.getResponseMessage(statusEnum, message, isTodayReservation);
        } catch (NullPointerException nullPointerException) {
            StatusEnum statusEnum = StatusEnum.NOT_FOUND;
            String message = nullPointerException.getMessage();
            return MakeResponseEntity.getResponseMessage(statusEnum, message);
        } catch (Exception e){
            StatusEnum statusEnum = StatusEnum.NOT_FOUND;
            String message = "알수없는 에러"  + e.getMessage();
            return MakeResponseEntity.getResponseMessage(statusEnum, message);
        }
    }

}
