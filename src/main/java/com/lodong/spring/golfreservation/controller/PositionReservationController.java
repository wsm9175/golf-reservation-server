package com.lodong.spring.golfreservation.controller;

import com.lodong.spring.golfreservation.dto.PositionLockDto;
import com.lodong.spring.golfreservation.dto.PositionReservationDto;
import com.lodong.spring.golfreservation.dto.ReservationDto;
import com.lodong.spring.golfreservation.responseentity.StatusEnum;
import com.lodong.spring.golfreservation.responseentity.service.PositionReservationService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.PropertyValueException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.Duration;
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
    public ResponseEntity<?> getReservationListByDateAndPosition(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            Map<Integer, List<PositionReservationDto>> positionReservationDtoList = positionReservationService
                    .getReservationListByDateAndPosition(date);
            StatusEnum statusEnum = StatusEnum.OK;
            String message = "각 타석 에 대한 시간 예약 정보";
            return getResponseMessage(statusEnum, message, positionReservationDtoList);
        } catch (NullPointerException nullPointerException) {
            StatusEnum statusEnum = StatusEnum.BAD_REQUEST;
            String message = nullPointerException.getMessage();
            return getResponseMessage(statusEnum, message);
        }
    }

    @PostMapping("/reservation")
    public ResponseEntity<?> reservation(@RequestBody ReservationDto reservation) {
        log.info("reservation info : " + reservation.toString());
        if (reservation.getDate() == null || reservation.getTime() == null || reservation.getCreateAt() == null || reservation.getUserId() == null) {
            StatusEnum statusEnum = StatusEnum.BAD_REQUEST;
            String message = "빈값이 존재합니다." + reservation;
            return getResponseMessage(statusEnum, message);
        }
        if (reservation.getDate().compareTo(getNowDate()) < 0) {
            StatusEnum statusEnum = StatusEnum.BAD_REQUEST;
            String message = reservation.getDate() + " " + reservation.getTime() + "의 시간은 지난 날짜이므로 예약이 불가능 합니다. " +
                    "현재 시간 : " + getNowDate() + " " + getNowTime();
            return getResponseMessage(statusEnum, message);
        }

        if (reservation.getDate().compareTo(getNowDate()) <= 0 && reservation.getTime().compareTo(getNowTime()) < 0) {
            StatusEnum statusEnum = StatusEnum.BAD_REQUEST;
            String message = reservation.getDate() + " " + reservation.getTime() + "의 시간은 지난 시간이므로 예약이 불가능 합니다. " +
                    "현재 시간 : " + getNowDate() + " " + getNowTime();
            return getResponseMessage(statusEnum, message);
        }

        if (reservation.getDate().compareTo(getNowDate()) <= 0 && Math.abs(Duration.between(reservation.getTime(), getNowTime()).getSeconds()) < 7200) {
            StatusEnum statusEnum = StatusEnum.BAD_REQUEST;
            String message = reservation.getDate() + " " + reservation.getTime() + "의 시간은 2시간 전이므로 예약이 불가능 합니다. " +
                    "현재 시간 : " + getNowDate() + " " + getNowTime()
                    + "duration : " + Duration.between(reservation.getTime(), getNowTime()).getSeconds();
            return getResponseMessage(statusEnum, message);
        }


        try {
            positionReservationService.reservation(reservation);
        } catch (NullPointerException nullPointerException) {
            StatusEnum statusEnum = StatusEnum.BAD_REQUEST;
            String message = nullPointerException.getMessage();
            return getResponseMessage(statusEnum, message);
        } catch (PropertyValueException propertyValueException) {
            StatusEnum statusEnum = StatusEnum.BAD_REQUEST;
            String message = propertyValueException.getMessage();
            return getResponseMessage(statusEnum, message);
        } catch (SQLIntegrityConstraintViolationException sqlIntegrityConstraintViolationException) {
            StatusEnum statusEnum = StatusEnum.BAD_REQUEST;
            String message = reservation.getTime() + "시간의 " + reservation.getPositionId() + "번 타석은 이미 예약되었습니다.";
            return getResponseMessage(statusEnum, message);
        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
            StatusEnum statusEnum = StatusEnum.BAD_REQUEST;
            String message = "하루에 한번의 예약만 가능합니다.";
            return getResponseMessage(statusEnum, message);
        }

        StatusEnum statusEnum = StatusEnum.OK;
        String message = reservation.getPositionId() + "번 타석 " + reservation.getDate() + " " + reservation.getTime() + " 예약 성공";
        return getResponseMessage(statusEnum, message);
    }

    /////////////////////web
    @PostMapping("/lock")
    public ResponseEntity<?> lockPosition(@RequestBody List<PositionLockDto> positionLock) {
        try {
            positionReservationService.lockPosition(positionLock);
            StatusEnum statusEnum = StatusEnum.OK;
            String message = "lock success";
            return getResponseMessage(statusEnum, message, null);
        } catch (Exception e) {
            StatusEnum statusEnum = StatusEnum.BAD_REQUEST;
            String message = "lock failed : " + e.getMessage();
            return getResponseMessage(statusEnum, message);
        }
    }
    @PostMapping("/unlock")
    public ResponseEntity<?> unlockPosition(@RequestBody List<PositionLockDto> positionLock) {
        try {
            positionReservationService.unLockPosition(positionLock);
            StatusEnum statusEnum = StatusEnum.OK;
            String message = "unlock success";
            return getResponseMessage(statusEnum, message, null);
        } catch (Exception e) {
            StatusEnum statusEnum = StatusEnum.BAD_REQUEST;
            String message = "unlock failed : " + e.getMessage();
            return getResponseMessage(statusEnum, message);
        }
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
