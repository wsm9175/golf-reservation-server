package com.lodong.spring.golfreservation.controller;

import com.lodong.spring.golfreservation.domain.lesson.Instructor;
import com.lodong.spring.golfreservation.domain.Position;
import com.lodong.spring.golfreservation.dto.LessonReservationDto;
import com.lodong.spring.golfreservation.dto.ReservationByInstructorDto;
import com.lodong.spring.golfreservation.dto.lesson.LessonReservationCheckDto;
import com.lodong.spring.golfreservation.responseentity.StatusEnum;
import com.lodong.spring.golfreservation.responseentity.service.LessonReservationService;
import com.lodong.spring.golfreservation.responseentity.service.PositionReservationService;
import com.lodong.spring.golfreservation.util.MakeResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.PropertyValueException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static com.lodong.spring.golfreservation.util.MakeResponseEntity.getResponseMessage;

@Slf4j
@RestController
@RequestMapping("rest/v1/lesson")
public class LessonReservationController {
    private final LessonReservationService lessonReservationService;
    private final PositionReservationService positionReservationService;
    private final ResourceLoader resourceLoader;

    public LessonReservationController(LessonReservationService lessonReservationService, PositionReservationService positionReservationService, @Autowired ResourceLoader resourceLoader) {
        this.lessonReservationService = lessonReservationService;
        this.positionReservationService = positionReservationService;
        this.resourceLoader = resourceLoader;
    }

    @GetMapping("/get/instructor-list")
    public ResponseEntity<?> getInstructorList() {
        List<Instructor> instructorList = lessonReservationService.getInstructorList();
        StatusEnum statusEnum = StatusEnum.OK;
        String message = "강사 리스트";
        return MakeResponseEntity.getResponseMessage(statusEnum, message, instructorList);
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<?> instructorFileDownload(@PathVariable String fileName) {
        //Resource resource = resourceLoader.getResource("classpath:static"+File.separator+ "images"+File.separator+"instructor" + File.separator + fileName);
        /* Resource resource = new ClassPathResource("static" + File.separator + "images" + File.separator + "instructor" + File.separator);*/
        try {
         /*   InputStream in = resource.getInputStream();
            File file = File.createTempFile(fileName, ".png");
            try (FileOutputStream fos = new FileOutputStream(file)) {
                int read;
                byte[] bytes = new byte[1024];

                while ((read = in.read(bytes)) != -1) {
                    fos.write(bytes, 0, read);
                }

                InputStream targetStream = new FileInputStream(file);
                System.out.println(file.getName());
                System.out.println(file.length());
                System.out.println(file.getAbsolutePath());
            }*/
            Resource resource = new ClassPathResource("static" + File.separator + "images" + File.separator + "instructor" + File.separator + fileName);
            InputStream is = resource.getInputStream();
            File tempFile = File.createTempFile(String.valueOf(is.hashCode()), ".png");
            tempFile.deleteOnExit();
            copyInputStreamToFile(is, tempFile);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, tempFile.getName())    //다운 받아지는 파일 명 설정
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(tempFile.length()))    //파일 사이즈 설정
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM.toString())    //바이너리 데이터로 받아오기 설정
                    .body(resource);    //파일 넘기기

        } catch (IOException e) {
            e.printStackTrace();
            StatusEnum statusEnum = StatusEnum.NOT_FOUND;
            String message = "강사 사진이 존재하지 않습니다.";
            return MakeResponseEntity.getResponseMessage(statusEnum, message);
        }
    }

    @GetMapping("/get/list")
    public ResponseEntity<?> getPositionList() {
        StatusEnum statusEnum = StatusEnum.OK;
        String message = "타석 리스트";
        List<Position> positionList = positionReservationService.getPositionList();
        return getResponseMessage(statusEnum, message, positionList);
    }

    @GetMapping("/get/reservation-info")
    public ResponseEntity<?> getReservationInfo(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, String instructorId) {
        try {
            List<LessonReservationCheckDto>  lessonReservationCheckDtos = lessonReservationService.getReservationListByDateAndInstructorId(date, instructorId);
            StatusEnum statusEnum = StatusEnum.OK;
            String message = "선택한 강사에 대한 시간 예약 정보";
            return getResponseMessage(statusEnum, message, lessonReservationCheckDtos);
        }catch (Exception e){
            StatusEnum statusEnum = StatusEnum.BAD_REQUEST;
            String message = e.getMessage();
            return getResponseMessage(statusEnum, message);
        }
    }

    @PostMapping("/do/reservation")
    public ResponseEntity<?> reservation(@RequestBody LessonReservationDto reservation) {
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
                    "현재 시간 : " + getNowDate() + " " + getNowTime();
            return getResponseMessage(statusEnum, message);
        }

        try {
            lessonReservationService.reservation(reservation);
        } catch (NullPointerException nullPointerException) {
            StatusEnum statusEnum = StatusEnum.BAD_REQUEST;
            String message = nullPointerException.getMessage();
            return getResponseMessage(statusEnum, message);
        } catch (PropertyValueException propertyValueException) {
            StatusEnum statusEnum = StatusEnum.BAD_REQUEST;
            String message = propertyValueException.getMessage();
            return getResponseMessage(statusEnum, message);
        }  catch (DataIntegrityViolationException dataIntegrityViolationException) {
            StatusEnum statusEnum = StatusEnum.BAD_REQUEST;
            String message = "하루에 한번의 예약만 가능합니다.";
            return getResponseMessage(statusEnum, message);
        } catch (SQLIntegrityConstraintViolationException e) {
            StatusEnum statusEnum = StatusEnum.BAD_REQUEST;
            String message = e.getMessage();
            return getResponseMessage(statusEnum, message);
        }

        StatusEnum statusEnum = StatusEnum.OK;
        String message = reservation.getDate() + " " + reservation.getTime() + " 예약 성공";
        return getResponseMessage(statusEnum, message);
    }

    ////////////////////Web
    @GetMapping("/get/reservation-list")
    public ResponseEntity<?> getReservationListByInstructorIdAndDate(String instructorId, @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<ReservationByInstructorDto> reservationByInstructorDtoList = lessonReservationService.getReservationListByInstructorIdAndDate(instructorId, date);
            StatusEnum statusEnum = StatusEnum.OK;
            String message = instructorId + " 강사 예약 정보";
            return getResponseMessage(statusEnum, message, reservationByInstructorDtoList);
        } catch (Exception e) {
            StatusEnum statusEnum = StatusEnum.BAD_REQUEST;
            String message = e.getMessage();
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

    private void copyInputStreamToFile(InputStream inputStream, File file) throws IOException {

        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            int read;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        }
    }

}
