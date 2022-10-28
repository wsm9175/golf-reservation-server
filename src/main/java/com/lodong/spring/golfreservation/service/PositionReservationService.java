package com.lodong.spring.golfreservation.service;

import com.lodong.spring.golfreservation.domain.*;
import com.lodong.spring.golfreservation.dto.PositionReservationDto;
import com.lodong.spring.golfreservation.dto.ReservationDto;
import com.lodong.spring.golfreservation.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.PropertyValueException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PositionReservationService {
    private final PositionRepository positionRepository;
    private final PositionReservationRepository positionReservationRepository;
    private final TimetableRepository timetableRepository;
    private final PositionTimeRepository positionTimeRepository;
    private final UserRepository userRepository;

    public List<Position> getPositionList() {
        return positionRepository.findAll();
    }


    public Map<Integer, List<PositionReservationDto>> getReservationListByDateAndPosition(LocalDate date) {
        //타석 목록
        List<Position> positionList = positionRepository.findAll();
        //타석의 타임 테이블 목록
        List<PositionTime> positionTimes = positionTimeRepository.findAll();
        //해당 날짜의 타석 예약정보
        List<PositionReservation> positionReservations = positionReservationRepository.findByDate(date);
        //각 포지션별 시간당 예약정보를 담을 변수 선언
        Map<Integer, List<PositionReservationDto>> reservationInfoByPosition = new HashMap<>();
        //예약정보 삽입
        //포지션에 대해(key) 각 시간 테이블을 가져와 해당 시간이 예약되었는지 확인 및 가공
        for (Position position : positionList) {
            //예약 목록을 가져온다.
            List<PositionReservationDto> positionReservationDtoList = new ArrayList<>();
            int positionId = position.getId();
            //해당 타석의 예약 가능 시간 정보를 가져온다.
            for (PositionTime positionTime : positionTimes.stream().filter(v -> v.getPosition().getId() == positionId).toList()) {
                PositionReservationDto positionReservationDto = new PositionReservationDto();
                Timetable positionTimeTable = positionTime.getTimeId();
                boolean isReservation = false;
                for (PositionReservation positionReservation : positionReservations) {
                    Timetable positionReservationTimeTable = positionReservation.getTime();
                    //예약된 시간과 포지션 예약 가능시간중 일치하는 시간이 있다면. + 포지션 아이디도 비교 해야함
                    if (positionTimeTable.getId().equals(positionReservationTimeTable.getId()) && positionId == positionReservation.getPositionId()) {
                        isReservation = true;
                    }
                }
                LocalTime startTime = positionTimeTable.getStartTime();
                LocalTime endTime = positionTimeTable.getEndTime();

                positionReservationDto.setStartTime(startTime);
                positionReservationDto.setEndTime(endTime);
                positionReservationDto.setReservation(isReservation);

                positionReservationDtoList.add(positionReservationDto);
            }
            reservationInfoByPosition.put(positionId, positionReservationDtoList);
        }

        return reservationInfoByPosition;
    }

    @Transactional
    public void reservation(ReservationDto reservationDto) throws
            NullPointerException, PropertyValueException, SQLIntegrityConstraintViolationException {
        //타임테이블로 부터 ID 추출
        Timetable time = timetableRepository.findByStartTime(reservationDto.getTime());
        if (time == null) {
            throw new NullPointerException(reservationDto.getTime() + " 시간은 DB에 없는 예약 시간입니다.");
        }
        //포지션과 입력된 예약 시간이 맞는지 여부 확인
        Position position = Position.builder()
                .id(reservationDto.getPositionId())
                .build();

        User user = userRepository.findById(reservationDto.getUserId()).orElseThrow(()->new NullPointerException("유저 정보가 잘못되었습니다."));

        if (positionTimeRepository.existsByPositionAndTimeId(position, time)) {
            //예약 실행
            PositionReservation positionReservation = PositionReservation.builder()
                    .id(UUID.randomUUID().toString())
                    .date(reservationDto.getDate())
                    .time(time)
                    .createAt(reservationDto.getCreateAt())
                    .positionId(reservationDto.getPositionId())
                    .userId(user)
                    .build();
            positionReservationRepository.save(positionReservation);
        } else {
            throw new NullPointerException(reservationDto.getTime() + "시간은 " + reservationDto.getPositionId() + "번 타석을 예약할 수 있는 시간이 아닙니다.");
        }
    }
}
