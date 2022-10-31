package com.lodong.spring.golfreservation.controller;

import com.lodong.spring.golfreservation.dto.PositionReservationDto;
import com.lodong.spring.golfreservation.dto.ReservationDto;
import com.lodong.spring.golfreservation.responseentity.StatusEnum;
import com.lodong.spring.golfreservation.service.PositionReservationService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.PropertyValueException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static com.lodong.spring.golfreservation.util.MakeResponseEntity.getResponseMessage;

@Slf4j
@RestController
@RequestMapping("rest/v1/position")
public class PositionReservationController {
    private final PositionReservationService positionReservationService;

    public PositionReservationController(PositionReservationService positionReservationService) {
        this.positionReservationService = positionReservationService;
    }

    @GetMapping("/get/reservation-list")
    public ResponseEntity<?> getReservationListByDateAndPosition(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate date) {
        Map<Integer, List<PositionReservationDto>> positionReservationDtoList = positionReservationService.getReservationListByDateAndPosition(date);
        StatusEnum statusEnum = StatusEnum.OK;
        String message = "각 타석 에 대한 시간 예약 정보";
        return getResponseMessage(statusEnum, message, positionReservationDtoList);
    }

    @PostMapping("/reservation")
    public ResponseEntity<?> reservation(@RequestBody ReservationDto reservation) {
        log.info("reservation info : " + reservation.toString());
        if (reservation.getDate() == null || reservation.getTime() == null || reservation.getCreateAt() == null || reservation.getUserId() == null) {
            StatusEnum statusEnum = StatusEnum.BAD_REQUEST;
            String message = "빈값이 존재합니다." + reservation;
            return getResponseMessage(statusEnum, message);
        }
        if(reservation.getDate().compareTo(getNowDate()) < 0){
            StatusEnum statusEnum = StatusEnum.BAD_REQUEST;
            String message = reservation.getDate() + " " + reservation.getTime() + "의 시간은 지난 시간이므로 예약이 불가능 합니다. " +
                    "현재 시간 : "  + getNowDate() + " " + getNowTime();
            return getResponseMessage(statusEnum, message);
        }

        if(reservation.getDate().compareTo(getNowDate()) <= 0 && reservation.getTime().compareTo(getNowTime()) < 0){
            StatusEnum statusEnum = StatusEnum.BAD_REQUEST;
            String message = reservation.getDate() + " " + reservation.getTime() + "의 시간은 지난 시간이므로 예약이 불가능 합니다. " +
                    "현재 시간 : "  + getNowDate() + " " + getNowTime();
            return getResponseMessage(statusEnum, message);
        }

        try {
            positionReservationService.reservation(reservation);
        } catch (NullPointerException nullPointerException) {
            StatusEnum statusEnum = StatusEnum.BAD_REQUEST;
            String message = nullPointerException.getMessage();
            return getResponseMessage(statusEnum, message);
        } catch (PropertyValueException propertyValueException){
            StatusEnum statusEnum = StatusEnum.BAD_REQUEST;
            String message = propertyValueException.getMessage();
            return getResponseMessage(statusEnum, message);
        } catch (SQLIntegrityConstraintViolationException sqlIntegrityConstraintViolationException){
            StatusEnum statusEnum = StatusEnum.BAD_REQUEST;
            String message = reservation.getTime() + "시간의 "+reservation.getPositionId() + "번 타석은 이미 예약되었습니다.";
            return getResponseMessage(statusEnum, message);
        }catch (DataIntegrityViolationException dataIntegrityViolationException){
            StatusEnum statusEnum = StatusEnum.BAD_REQUEST;
            String message = "하루에 한번의 예약만 가능합니다.";
            return getResponseMessage(statusEnum, message);
        }

        StatusEnum statusEnum = StatusEnum.OK;
        String message = reservation.getPositionId() + "번 타석 " + reservation.getDate() + " " + reservation.getTime() + " 예약 성공";
        return getResponseMessage(statusEnum, message);
    }

    private LocalDate getNowDate() {
        LocalDate now = LocalDate.now();
        return now;
    }

    private LocalTime getNowTime() {
        LocalTime now = LocalTime.now();
        return now;
    }

}
