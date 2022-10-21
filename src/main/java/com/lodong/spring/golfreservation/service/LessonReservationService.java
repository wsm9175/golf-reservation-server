package com.lodong.spring.golfreservation.service;

import com.lodong.spring.golfreservation.domain.*;
import com.lodong.spring.golfreservation.dto.LessonReservationDto;
import com.lodong.spring.golfreservation.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.PropertyValueException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LessonReservationService {
    private final InstructorRepository instructorRepository;
    private final TimetableRepository timetableRepository;
    private final PositionTimeRepository positionTimeRepository;
    private final PositionReservationRepository positionReservationRepository;
    private final LessonReservationRepository lessonReservationRepository;

    private final UserRepository userRepository;

    public List<Instructor> getInstructorList(){
        //강사 목록을 불러온다.
        List<Instructor> instructorList = instructorRepository.findAll();

        //해당 객체 리스트를 보낸다.
        return instructorList;
    }

    @Transactional
    public void reservation(LessonReservationDto reservationDto) throws NullPointerException, PropertyValueException, SQLIntegrityConstraintViolationException {
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

        Instructor instructor = instructorRepository.findById(reservationDto.getInstructorId()).orElseThrow(()->new NullPointerException("강사 정보가 잘못되었습니다."));

        if (positionTimeRepository.existsByPositionAndTimeId(position, time)) {
            //포지션 예약 우선 실행
            PositionReservation positionReservation = PositionReservation.builder()
                    .id(UUID.randomUUID().toString())
                    .date(reservationDto.getDate())
                    .time(time)
                    .createAt(reservationDto.getCreateAt())
                    .positionId(reservationDto.getPositionId())
                    .userId(user)
                    .build();
            positionReservationRepository.save(positionReservation);


            //성공시 레슨 예약 실행
            LessonReservation lessonReservation = LessonReservation.builder()
                    .id(UUID.randomUUID().toString())
                    .date(reservationDto.getDate())
                    .time(time)
                    .createAt(reservationDto.getCreateAt())
                    .positionReservation(positionReservation)
                    .user(user)
                    .instructor(instructor)
                    .build();

            log.info(lessonReservation.toString());

            lessonReservationRepository.save(lessonReservation);

        } else {
            throw new NullPointerException(reservationDto.getTime() + "시간은 " + reservationDto.getPositionId() + "번 타석을 예약할 수 있는 시간이 아닙니다.");
        }
    }
}
