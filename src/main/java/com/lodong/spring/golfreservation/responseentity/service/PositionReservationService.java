package com.lodong.spring.golfreservation.responseentity.service;

import com.lodong.spring.golfreservation.domain.*;
import com.lodong.spring.golfreservation.domain.delete.*;
import com.lodong.spring.golfreservation.domain.lesson.LessonReservation;
import com.lodong.spring.golfreservation.dto.lesson.LessonReservationNotiDto;
import com.lodong.spring.golfreservation.dto.position.PositionLockDto;
import com.lodong.spring.golfreservation.dto.position.PositionReservationDeleteDto;
import com.lodong.spring.golfreservation.dto.position.PositionReservationDto;
import com.lodong.spring.golfreservation.dto.ReservationDto;
import com.lodong.spring.golfreservation.dto.position.PositionReservationNotiDto;
import com.lodong.spring.golfreservation.repository.*;
import com.lodong.spring.golfreservation.util.DateUtil;
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

    private final HolidayRepository holidayRepository;

    private final PositionLockRepository positionLockRepository;

    private final LocalTime sat = LocalTime.of(22, 00, 00);
    private final LocalTime sunStart = LocalTime.of(8, 00, 00);
    private final LocalTime sunEnd = LocalTime.of(22, 00, 00);

    public List<Position> getPositionList() {
        return positionRepository.findAll();
    }


    public Map<Integer, List<PositionReservationDto>> getReservationListByDateAndInstructorId(LocalDate date) throws NullPointerException {

        //타석 목록
        List<Position> positionList = positionRepository.findAll();
        //타석의 타임 테이블 목록 일요일 || 토요일 || 공휴일을 구분한다.
        List<PositionTime> positionTimes = null;
        List<Holiday> holidayList = holidayRepository.findAll();

        //토요일
        if (DateUtil.dayOfWeek(date) == 6)
            positionTimes = positionTimeRepository.findAllByTimeId_StartTimeLessThanOrderByTimeId_StartTime(sat).orElseThrow(NullPointerException::new);
            //일요일
        else if (DateUtil.dayOfWeek(date) == 7) {
            positionTimes = positionTimeRepository.findAllByTimeId_StartTimeGreaterThanAndTimeId_EndTimeLessThanOrderByTimeId_StartTime(sunStart, sunEnd).orElseThrow(NullPointerException::new);
            int year = date.getYear();
            int month = date.getMonthValue();
            int day = date.getDayOfMonth();
            int week = DateUtil.getCurrentWeekOfMonth(year, month, day);
            System.out.println("week : " + week);
            if (week == 3 || week == 1) {
                throw new NullPointerException("1, 3주차 일요일은 예약이 불가능 합니다.");
            }
        } else {
            positionTimes = positionTimeRepository.findAll();
        }
        //공휴일
        for (Holiday holiday : holidayList) {
            if (holiday.getId().equals(date.toString())) {
                positionTimes = positionTimeRepository.findAllByTimeId_StartTimeGreaterThanAndTimeId_EndTimeLessThanOrderByTimeId_StartTime(sunStart, sunEnd)
                        .orElseThrow(NullPointerException::new);
                break;
            }
        }

        //해당 날짜의 타석 예약정보
        List<PositionReservation> positionReservations = positionReservationRepository.findByDate(date).orElse(new ArrayList<>());
        //각 포지션별 시간당 예약정보를 담을 변수 선언
        Map<Integer, List<PositionReservationDto>> reservationInfoByPosition = new HashMap<>();
        //예약정보 삽입
        //포지션에 대해(key) 각 시간 테이블을 가져와 해당 시간이 예약되었는지 확인 및 가공
        for (Position position : positionList) {
            //예약 목록을 가져온다.
            List<PositionReservationDto> positionReservationDtoList = new ArrayList<>();
            int positionId = position.getId();
            List<PositionLock> positionLockList = position.getPositionLockList();
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
                        positionReservationDto.setReservationMemberName(positionReservation.getUserId().getName());
                    }
                }
                LocalTime startTime = positionTimeTable.getStartTime();
                LocalTime endTime = positionTimeTable.getEndTime();

                positionReservationDto.setStartTime(startTime);
                positionReservationDto.setEndTime(endTime);
                positionReservationDto.setReservation(isReservation);
                positionReservationDto.setLock(false);
                positionReservationDtoList.add(positionReservationDto);
                for(PositionLock positionLock:positionLockList){
                    //lock 시작과
                    if(positionLock.getTimetable().getStartTime().equals(startTime) && positionLock.getDate().equals(date)){
                        positionReservationDto.setLock(true);
                        break;
                    }
                }
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

        User user = userRepository.findById(reservationDto.getUserId()).orElseThrow(() -> new NullPointerException("유저 정보가 잘못되었습니다."));

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

    @Transactional
    public void lockPosition(List<PositionLockDto> positionLockDtos) {
        //타임테이블 id 조회 및 삽입
        ArrayList<PositionLock> positionLocks = new ArrayList<>();
        for (PositionLockDto positionLockDto : positionLockDtos) {
            Timetable timetable = timetableRepository.findByStartTime(positionLockDto.getTime());
            Position position = positionRepository.findById(positionLockDto.getPositionId()).orElseThrow(()->new NullPointerException("없는 타석입니다."));
            PositionLock positionLock = PositionLock.builder()
                    .id(UUID.randomUUID().toString())
                    .position(position)
                    .date(positionLockDto.getDate())
                    .timetable(timetable)
                    .build();
            positionLocks.add(positionLock);
        }

        positionLockRepository.saveAll(positionLocks);
    }

    @Transactional
    public void unLockPosition(List<PositionLockDto> positionLockDtos){
        for(PositionLockDto positionLockDto : positionLockDtos){
            positionLockRepository.deleteByPositionIdAndDateAndTimetableStartTime(
                    positionLockDto.getPositionId(),
                    positionLockDto.getDate(),
                    positionLockDto.getTime());
        }
    }

    public List<PositionReservationNotiDto> getNotification(LocalDate todayDate){
        List<PositionReservation> positionReservations = positionReservationRepository
                .findByDate(todayDate)
                .orElseThrow(()-> new NullPointerException());

        List<PositionReservationNotiDto> positionReservationNotiDtos = new ArrayList<>();

        for(PositionReservation positionReservation : positionReservations){
            PositionReservationNotiDto positionReservationNotiDto = new PositionReservationNotiDto();
            positionReservationNotiDto.setReservationDate(positionReservation.getDate());
            positionReservationNotiDto.setReservationTime(positionReservation.getTime().getStartTime());
            positionReservationNotiDto.setReservationEndTime(positionReservation.getTime().getEndTime());
            positionReservationNotiDto.setCreateAt(positionReservation.getCreateAt());
            positionReservationNotiDto.setPositionId(positionReservation.getPositionId());
            positionReservationNotiDto.setCustomerName(positionReservation.getUserId().getName());
            positionReservationNotiDtos.add(positionReservationNotiDto);
        }

        return positionReservationNotiDtos;
    }

    @Transactional
    public void deletePositionReservation(PositionReservationDeleteDto positionReservationDeleteDto){
        positionReservationRepository
                .deleteByDateAndTime_StartTimeAndPositionId(positionReservationDeleteDto.getDate(), positionReservationDeleteDto.getTime(), positionReservationDeleteDto.getPositionId());
    }

}
