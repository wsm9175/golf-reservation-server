package com.lodong.spring.golfreservation.responseentity.service;

import com.lodong.spring.golfreservation.domain.*;
import com.lodong.spring.golfreservation.domain.lesson.*;
import com.lodong.spring.golfreservation.dto.LessonReservationDto;
import com.lodong.spring.golfreservation.dto.ReservationByInstructorDto;
import com.lodong.spring.golfreservation.dto.lesson.LessonLockDto;
import com.lodong.spring.golfreservation.dto.lesson.LessonReservationCheckDto;
import com.lodong.spring.golfreservation.dto.lesson.LessonReservationDeleteDto;
import com.lodong.spring.golfreservation.dto.lesson.LessonReservationNotiDto;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LessonReservationService {
    private final InstructorRepository instructorRepository;
    private final LessonReservationRepository lessonReservationRepository;
    private final InstructorTimeRepository instructorTimeRepository;

    private final HolidayRepository holidayRepository;
    private final UserRepository userRepository;

    private final LessonLockRespository lessonLockRespository;

    private final LocalTime sat = LocalTime.of(22, 00, 00);
    private final LocalTime sunStart = LocalTime.of(8, 59, 00);
    private final LocalTime sunEnd = LocalTime.of(22, 00, 00);

    public List<Instructor> getInstructorList() {
        //강사 목록을 불러온다.
        List<Instructor> instructorList = instructorRepository.findAll();

        //해당 객체 리스트를 보낸다.
        return instructorList;
    }

    @Transactional
    public List<LessonReservationCheckDto> getReservationListByDateAndInstructorId(LocalDate date, String instructorId) {
        //해당 강사의 예약 가능 시간 목록을 가져옴 토요일, 일요일, 공휴일을 구분한다.
        //타석의 타임 테이블 목록 일요일 || 토요일 || 공휴일을 구분한다.
        List<InstructorTime> lessonTimeTableList = null;
        List<Holiday> holidayList = holidayRepository.findAll();
        //토요일
        if (DateUtil.dayOfWeek(date) == 6)
            lessonTimeTableList = instructorTimeRepository
                    .findAllByInstructorIdAndLessonTimeTable_StartTimeLessThanOrderByLessonTimeTable_StartTime(instructorId, sat)
                    .orElseThrow(NullPointerException::new);
            //일요일
        else if (DateUtil.dayOfWeek(date) == 7) {
            lessonTimeTableList = instructorTimeRepository
                    .findAllByInstructorIdAndLessonTimeTable_StartTimeGreaterThanAndLessonTimeTable_EndTimeLessThanOrderByLessonTimeTable_StartTime(instructorId, sunStart, sunEnd)
                    .orElseThrow(NullPointerException::new);
            int year = date.getYear();
            int month = date.getMonthValue();
            int day = date.getDayOfMonth();
            int week = DateUtil.getCurrentWeekOfMonth(year, month, day);
            System.out.println("week : " + week);
            if (week == 3 || week == 1) {
                throw new NullPointerException("1, 3주차 일요일은 예약이 불가능 합니다.");
            }
        } else {
            lessonTimeTableList = instructorTimeRepository
                    .findByInstructorIdOrderByLessonTimeTable_StartTime(instructorId)
                    .orElseThrow(()-> new NullPointerException("해당 강사는 시간이 정해져있지 않습니다."));
        }
        //공휴일
        for (Holiday holiday : holidayList) {
            if (holiday.getId().equals(date.toString())) {
                lessonTimeTableList = instructorTimeRepository.findAllByInstructorIdAndLessonTimeTable_StartTimeGreaterThanAndLessonTimeTable_EndTimeLessThanOrderByLessonTimeTable_StartTime(instructorId, sunStart, sunEnd)
                        .orElseThrow(NullPointerException::new);
                break;
            }
        }

        //해당 강사의 예약 목록을 가져옴.
        List<LessonReservation> lessonReservations = lessonReservationRepository
                .findByDateAndInstructorId(date, instructorId)
                .orElseThrow(() -> new NullPointerException("해당 강사는 예약 목록이 존재하지 않습니다."));
        //해당 강사의 date 및 id를 기준으로 Lock리스트를 가져옴
        List<LessonLock> lessonLocks = lessonLockRespository
                .findByDateAndInstructorTime_Instructor_Id(date, instructorId)
                .orElse(new ArrayList<>());

        List<LessonReservationCheckDto> lessonReservationCheckDtos = new ArrayList<>();
        //예약 목록을 이용해서 전송 데이터 셋팅
        for (InstructorTime instructorTime : lessonTimeTableList) {
            LessonReservationCheckDto lessonReservationCheckDto = new LessonReservationCheckDto();
            lessonReservationCheckDto.setStartTime(instructorTime.getLessonTimeTable().getStartTime());
            lessonReservationCheckDto.setEndTime(instructorTime.getLessonTimeTable().getEndTime());
            //예비로 일단 처리

            for (LessonReservation lessonReservation : lessonReservations) {
                if (lessonReservation.getTime().getLessonTimeTable().getStartTime().equals(instructorTime.getLessonTimeTable().getStartTime())) {
                    lessonReservationCheckDto.setReservationMemberName(lessonReservation.getUser().getName());
                    lessonReservationCheckDto.setReservation(true);
                    break;
                }

            }
            lessonReservationCheckDto.setLock(false);
            for (LessonLock lessonLock : lessonLocks) {
                // 날짜 일치 및 시간 일치 및 강사 아이디가 일치시 lock
                if (lessonLock.getDate().equals(date) &&
                        lessonLock.getInstructorTime().getLessonTimeTable().getStartTime().equals(instructorTime.getLessonTimeTable().getStartTime()) &&
                        lessonLock.getInstructorTime().getInstructor().getId().equals(instructorTime.getInstructor().getId())) {
                    lessonReservationCheckDto.setLock(true);

                }
            }
            lessonReservationCheckDtos.add(lessonReservationCheckDto);
        }

        return lessonReservationCheckDtos;
    }

    @Transactional
    public void reservation(LessonReservationDto reservationDto) throws NullPointerException, PropertyValueException, SQLIntegrityConstraintViolationException {
        //타임테이블로 부터 ID 추출
        InstructorTime instructorTime = instructorTimeRepository
                .findByLessonTimeTable_StartTimeAndInstructorId(reservationDto.getTime(), reservationDto.getInstructorId())
                .orElseThrow(() -> new NullPointerException("해당 시간은 등록되지 않은 시간입니다."));
        if (instructorTime == null) {
            throw new NullPointerException(reservationDto.getTime() + " 시간은 DB에 없는 예약 시간입니다.");
        }


        User user = userRepository.findById(reservationDto.getUserId()).orElseThrow(() -> new NullPointerException("유저 정보가 잘못되었습니다."));

        Instructor instructor = instructorRepository.findById(reservationDto.getInstructorId()).orElseThrow(() -> new NullPointerException("강사 정보가 잘못되었습니다."));

        //레슨 예약 실행
        LessonReservation lessonReservation = LessonReservation.builder()
                .id(UUID.randomUUID().toString())
                .date(reservationDto.getDate())
                .time(instructorTime)
                .createAt(reservationDto.getCreateAt())
                .user(user)
                .instructor(instructor)
                .build();

        log.info(lessonReservation.toString());

        lessonReservationRepository.save(lessonReservation);
    }
    @Transactional
    public List<ReservationByInstructorDto> getReservationListByInstructorIdAndDate(String instructorId, LocalDate date) {
        List<LessonReservation> lessonReservations = lessonReservationRepository
                .findByDateAndInstructorId(date, instructorId)
                .orElseThrow(() -> new NullPointerException("해당 강사의 레슨 예약정보가 존재하지 않습니다."));

        List<ReservationByInstructorDto> reservationByInstructorDtoList = new ArrayList<>();

        lessonReservations.forEach(lessonReservation -> {
            ReservationByInstructorDto reservationByInstructorDto = new ReservationByInstructorDto();
            reservationByInstructorDto.setLessonId(lessonReservation.getId());
            reservationByInstructorDto.setStartTime(lessonReservation.getTime().getLessonTimeTable().getStartTime());
            reservationByInstructorDto.setEndTime(lessonReservation.getTime().getLessonTimeTable().getEndTime());
            reservationByInstructorDto.setCustomerName(lessonReservation.getUser().getName());
            reservationByInstructorDtoList.add(reservationByInstructorDto);
        });

        return reservationByInstructorDtoList;
    }
    @Transactional
    public void lockLesson(List<LessonLockDto> lessonLockDtos){
        ArrayList<LessonLock> lessonLocks = new ArrayList<>();
        for(LessonLockDto lessonLockDto:lessonLockDtos){
            InstructorTime instructorTime = instructorTimeRepository
                    .findByLessonTimeTable_StartTimeAndInstructorId(lessonLockDto.getTime(),lessonLockDto.getInstructorId())
                    .orElseThrow(()->new NullPointerException("해당 시간은 존재하지 않습니다."));
            LessonLock lessonLock = LessonLock.builder()
                    .id(UUID.randomUUID().toString())
                    .instructorTime(instructorTime)
                    .date(lessonLockDto.getDate())
                    .build();
            lessonLocks.add(lessonLock);
        }

        lessonLockRespository.saveAll(lessonLocks);
    }

    @Transactional
    public void unLockLesson(List<LessonLockDto> lessonLockDtos){
        for(LessonLockDto lessonLockDto : lessonLockDtos){
            LessonLock lessonLock = lessonLockRespository
                    .findByDateAndInstructorTime_LessonTimeTable_StartTimeAndInstructorTime_Instructor_Id(lessonLockDto.getDate(),lessonLockDto.getTime(), lessonLockDto.getInstructorId())
                    .orElseThrow(()->new NullPointerException("해당 잠금은 존재하지 않습니다."));
            lessonLockRespository.delete(lessonLock);
        }
    }

    @Transactional
    public void deleteLesson(LessonReservationDeleteDto lessonReservationDeleteDto){
        lessonReservationRepository
                .deleteByDateAndTime_LessonTimeTable_StartTimeAndInstructor_Id(lessonReservationDeleteDto.getDate(), lessonReservationDeleteDto.getTime(), lessonReservationDeleteDto.getInstructorId());
    }

    public List<LessonReservationNotiDto> getNotification(LocalDate todayDate){
        List<LessonReservation> lessonReservations = lessonReservationRepository
                .findByDate(todayDate)
                .orElseThrow(()-> new NullPointerException());

        List<LessonReservationNotiDto> lessonReservationNotiDtos = new ArrayList<>();

        for(LessonReservation lessonReservation : lessonReservations){
            LessonReservationNotiDto lessonReservationNotiDto = new LessonReservationNotiDto();
            lessonReservationNotiDto.setReservationDate(lessonReservation.getDate());
            lessonReservationNotiDto.setReservationStartTime(lessonReservation.getTime().getLessonTimeTable().getStartTime());
            lessonReservationNotiDto.setReservationEndTime(lessonReservation.getTime().getLessonTimeTable().getEndTime());
            lessonReservationNotiDto.setCreateAt(lessonReservation.getCreateAt());
            lessonReservationNotiDto.setInstructorName(lessonReservation.getInstructor().getName());
            lessonReservationNotiDto.setCustomerName(lessonReservation.getUser().getName());
            lessonReservationNotiDtos.add(lessonReservationNotiDto);
        }

        return lessonReservationNotiDtos;
    }
}
