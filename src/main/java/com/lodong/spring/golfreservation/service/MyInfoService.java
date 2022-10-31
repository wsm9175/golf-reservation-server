package com.lodong.spring.golfreservation.service;

import com.lodong.spring.golfreservation.domain.*;
import com.lodong.spring.golfreservation.dto.CancelReservationDto;
import com.lodong.spring.golfreservation.dto.MyLessonReservationInfoDto;
import com.lodong.spring.golfreservation.dto.MyPositionReservationInfoDto;
import com.lodong.spring.golfreservation.repository.CancelReservationRepository;
import com.lodong.spring.golfreservation.repository.LessonReservationRepository;
import com.lodong.spring.golfreservation.repository.PositionReservationRepository;
import com.lodong.spring.golfreservation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyInfoService {
    private final UserRepository userRepository;
    private final PositionReservationRepository positionReservationRepository;
    private final LessonReservationRepository lessonReservationRepository;

    private final CancelReservationRepository cancelReservationRepository;

    private final String RESERVATION_POSITION = "타석";
    private final String RESERVATION_LESSON = "레슨";

    public User getMyInfo(String uid) throws NullPointerException {
        return userRepository.findById(uid).orElseThrow(() -> new NullPointerException("없는 유저 정보입니다."));
    }

    public HashMap<String, List<?>> getMyReservationList(String uid) {
        User user = userRepository.findById(uid).orElseThrow(() -> new NullPointerException("없는 유저 정보입니다."));

        //타석 예약 정보 및 세팅
        List<PositionReservation> positionReservationList = positionReservationRepository.findByUserId(user);
        List<MyPositionReservationInfoDto> myPositionReservationInfoDtoList = new ArrayList<>();

        for (PositionReservation positionReservation : positionReservationList) {
            MyPositionReservationInfoDto myPositionReservationInfoDto = new MyPositionReservationInfoDto();
            myPositionReservationInfoDto.setId(positionReservation.getId());
            myPositionReservationInfoDto.setDate(positionReservation.getDate());
            myPositionReservationInfoDto.setStartTime(positionReservation.getTime().getStartTime());
            myPositionReservationInfoDto.setEndTime(positionReservation.getTime().getEndTime());
            myPositionReservationInfoDto.setCreateAt(positionReservation.getCreateAt());
            myPositionReservationInfoDto.setPositionId(positionReservation.getPositionId());
            myPositionReservationInfoDtoList.add(myPositionReservationInfoDto);
        }

        //레슨 예약 정보 및 세팅
        List<LessonReservation> lessonReservationList = lessonReservationRepository.findByUser(user);
        List<MyLessonReservationInfoDto> myLessonReservationInfoDtoList = new ArrayList<>();

        for (LessonReservation lessonReservation : lessonReservationList) {
            MyLessonReservationInfoDto lessonReservationInfoDto = new MyLessonReservationInfoDto();
            lessonReservationInfoDto.setId(lessonReservation.getId());
            lessonReservationInfoDto.setDate(lessonReservation.getDate());
            lessonReservationInfoDto.setStartTime(lessonReservation.getTime().getStartTime());
            lessonReservationInfoDto.setEndTime(lessonReservation.getTime().getEndTime());
            lessonReservationInfoDto.setCreateAt(lessonReservation.getCreateAt());
            lessonReservationInfoDto.setPositionId(lessonReservationInfoDto.getPositionId());
            lessonReservationInfoDto.setInstructor(lessonReservation.getInstructor());
            myLessonReservationInfoDtoList.add(lessonReservationInfoDto);
        }

        HashMap<String, List<?>> myReservationInfo = new HashMap<>();
        myReservationInfo.put(RESERVATION_POSITION, myPositionReservationInfoDtoList);
        myReservationInfo.put(RESERVATION_LESSON, myLessonReservationInfoDtoList);

        return myReservationInfo;
    }

    @Transactional
    public void cancelReservation(CancelReservationDto cancelReservationDto) throws RuntimeException {
        //User 유효성 검사
        User loginUser = userRepository.findById(cancelReservationDto.getUserId()).orElseThrow(() -> new RuntimeException("없는 유저 정보 입니다."));

        //당일 예약 취소 불가능
        /*boolean isTodayCancel = cancelReservationRepository.existsByUserAndCreateAt(loginUser, getNowDate());
        if (isTodayCancel) {
            throw new RuntimeException("취소 당일 재예약후 예약 취소는 불가능 합니다.");
        }*/
        List<CancelReservation> cancelReservations = cancelReservationRepository.findByUserAndCreateAt(loginUser, getNowDate());
        if(cancelReservations.size() >= 2){
            throw new RuntimeException("당일 예약 취소는 두번까지 가능합니다.");
        }

        //해당 유저가 당일 두시간 전에 취소했는지 확인
        //삭제 수행
        if (cancelReservationDto.getReservationType().equals(RESERVATION_POSITION)) {
            PositionReservation positionReservation = positionReservationRepository.findById(cancelReservationDto.getReservationId()).orElseThrow(() -> new RuntimeException("없는 예약입니다."));
            User user = positionReservation.getUserId();
            if (user.getId().equals(cancelReservationDto.getUserId())) {
                LocalDate reservationDate = positionReservation.getDate();
                LocalTime reservationStartTime = positionReservation.getTime().getStartTime();

                if (reservationDate.compareTo(getNowDate()) == 0) {
                    Duration duration = Duration.between(getNowTime(), reservationStartTime);
                    if (duration.getSeconds() < 7200) {
                        throw new RuntimeException("예약 2시간 전에는 취소가 불가능 합니다.");
                    }
                }

                positionReservationRepository.deleteById(cancelReservationDto.getReservationId());
                CancelReservation cancelReservation = CancelReservation.builder()
                        .id(positionReservation.getId())
                        .user(positionReservation.getUserId())
                        .date(positionReservation.getDate())
                        .time(positionReservation.getTime())
                        .positionId(positionReservation.getPositionId())
                        .createAt(getNowDate())
                        .build();
                log.info(cancelReservation.toString());
                cancelReservationRepository.save(cancelReservation);
            } else {
                throw new RuntimeException("해당 유저가 예약한 값이 아닙니다.");
            }
        } else if (cancelReservationDto.getReservationType().equals(RESERVATION_LESSON)) {
            LessonReservation lessonReservation = lessonReservationRepository.findById(cancelReservationDto.getReservationId()).orElseThrow(() -> new RuntimeException("없는 예약입니다."));
            User user = lessonReservation.getUser();

            String positionReservationId = lessonReservationRepository.
                    findById(cancelReservationDto.getReservationId()).orElseThrow(() -> new RuntimeException("삭제된 예약입니다.")).getPositionReservation().getId();

            if (user.getId().equals(cancelReservationDto.getUserId())) {
                LocalDate reservationDate = lessonReservation.getDate();
                LocalTime reservationStartTime = lessonReservation.getTime().getStartTime();

                if (reservationDate.compareTo(getNowDate()) == 0) {
                    Duration duration = Duration.between(getNowTime(), reservationStartTime);
                    if (duration.getSeconds() < 7200) {
                        throw new RuntimeException("예약 2시간 전에는 취소가 불가능 합니다.");
                    }
                }
                lessonReservationRepository.deleteById(cancelReservationDto.getReservationId());
                positionReservationRepository.deleteById(positionReservationId);
                //positionReservationRepository.deleteById(cancelReservationDto.getReservationId());
                CancelReservation cancelReservation = CancelReservation.builder()
                        .id(lessonReservation.getId())
                        .user(lessonReservation.getUser())
                        .date(lessonReservation.getDate())
                        .time(lessonReservation.getTime())
                        .positionId(lessonReservation.getPositionReservation().getPositionId())
                        .createAt(getNowDate())
                        .build();
                cancelReservationRepository.save(cancelReservation);


            } else {
                throw new RuntimeException("해당 유저가 예약한 값이 아닙니다.");
            }
        } else {
            throw new RuntimeException("잘못된 예약 타입입니다.");
        }
    }

    public boolean isReservationToday(String uid, LocalDate date){
        User loginUser = userRepository.findById(uid).orElseThrow(() -> new RuntimeException("없는 유저 정보 입니다."));
        return positionReservationRepository.existsByUserIdAndDate(loginUser, date);
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
