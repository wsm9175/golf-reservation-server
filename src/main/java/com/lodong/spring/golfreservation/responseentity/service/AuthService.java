package com.lodong.spring.golfreservation.responseentity.service;

import com.lodong.spring.golfreservation.domain.Admin;
import com.lodong.spring.golfreservation.domain.ChangePassword;
import com.lodong.spring.golfreservation.domain.User;
import com.lodong.spring.golfreservation.dto.AdminLoginDto;
import com.lodong.spring.golfreservation.dto.LoginDto;
import com.lodong.spring.golfreservation.repository.AdminRepository;
import com.lodong.spring.golfreservation.repository.ChangePasswordRepository;
import com.lodong.spring.golfreservation.repository.UserRepository;
import com.sun.jdi.request.DuplicateRequestException;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.PropertyValueException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ChangePasswordRepository changePasswordRepository;
    private final AdminRepository adminRepository;


    public User auth(LoginDto loginDto) throws NullPointerException, IllegalStateException, IllegalArgumentException {
        String id = loginDto.getUserId();
        String password = loginDto.getPassword();

        User user = userRepository.findByUserId(id).orElseThrow(NullPointerException::new);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalAccessError("비밀번호가 불일치 합니다.");
        }
        return user;
    }

    @Transactional
    public void registration(User user) throws DuplicateRequestException, PropertyValueException {
        if (userRepository.existsByUserId(user.getUserId())) {
            log.info("이미 존재하는 아이디입니다.");
            throw new DuplicateRequestException("이미 존재하는 아이디입니다.");
        } else if (userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            log.info("이미 존재하는 전화번호입니다.");
            throw new DuplicateRequestException("이미 존재하는 전화번호입니다.");
        }
        userRepository.save(user);
    }

    public String findIdUsingPhoneNumber(String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber).orElseThrow(() -> new NullPointerException("해당 휴대폰 번호는 등록된 정보가 아닙니다."));
        return user.getUserId();
    }

    @Transactional
    public String findPasswordByPhoneNumberAndId(String phoneNumber, String userId) throws NullPointerException{
        userRepository.findByPhoneNumberAndUserId(phoneNumber, userId)
                .orElseThrow(() -> new NullPointerException("해당 휴대폰번호 혹은 아이디는 등록된 정보가 아닙니다."));

        String uuid = UUID.randomUUID().toString();
        LocalDateTime localDateTime = LocalDateTime.now();

        ChangePassword changePassword = ChangePassword.builder()
                .id(uuid)
                .userId(userId)
                .phoneNumber(phoneNumber)
                .createAt(localDateTime)
                .build();

        changePasswordRepository.save(changePassword);

        return changePassword.getId();
    }

    public Admin authAdmin(AdminLoginDto adminLoginDto){
        Admin admin = adminRepository
                .findByAdminIdAndPassword(adminLoginDto.getUserId(), adminLoginDto.getPassword())
                .orElseThrow(()->new NullPointerException("해당 admin은 존재하지 않습니다."));
        return admin;
    }

    @Transactional
    public void alterPassword(String uuid, String changePassword, String userId, String phoneNumber) throws NullPointerException {
        //존재하는 세션인지 검사.
        ChangePassword changePasswordEntity = changePasswordRepository.findById(uuid)
                .orElseThrow(() -> new NullPointerException("존재하지 않는 세션입니다."));
        //입력된 모든 값 비교
        if (!changePasswordEntity.getUserId().equals(userId)) {
            throw new NullPointerException("아이디가 유효하지 않습니다.");
        }

        if (!changePasswordEntity.getPhoneNumber().equals(phoneNumber)) {
            throw new NullPointerException("휴대폰 번호가 유효하지 않습니다.");
        }

        //해당 세션이 3분 이내의 세션인지 검사
        LocalDateTime nowTime = LocalDateTime.now();
        LocalDateTime sessionTime = changePasswordEntity.getCreateAt();

        long differ = compareHour(sessionTime,nowTime);
        log.info("differ : " + differ);
        if(differ > 180){
            throw new NullPointerException("세션이 유효하지 않습니다.");
        }
        //유저 비밀번호  수정
        userRepository.updateUser(passwordEncoder.encode(changePassword), userId);
    }

    public long compareHour(LocalDateTime date1, LocalDateTime date2) {
       /* LocalDateTime dayDate1 = date1.truncatedTo(ChronoUnit.MINUTES);
        LocalDateTime dayDate2 = date2.truncatedTo(ChronoUnit.MINUTES);
        int compareResult = dayDate1.compareTo(dayDate2);
        System.out.println("date1.truncatedTo(ChronoUnit.MIN) : " + dayDate1);
        System.out.println("date2.truncatedTo(ChronoUnit.MIN) : " + dayDate2);
        System.out.println("결과 : " + compareResult);*/
        Duration duration = Duration.between(date1, date2);
        return duration.getSeconds();
    }
}
