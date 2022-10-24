package com.lodong.spring.golfreservation.controller;

import com.lodong.spring.golfreservation.domain.User;
import com.lodong.spring.golfreservation.dto.ChangePasswordDto;
import com.lodong.spring.golfreservation.dto.LoginDto;
import com.lodong.spring.golfreservation.dto.RegistrationDto;
import com.lodong.spring.golfreservation.responseentity.StatusEnum;
import com.lodong.spring.golfreservation.service.AuthService;
import com.sun.jdi.request.DuplicateRequestException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.PropertyValueException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


import static com.lodong.spring.golfreservation.util.MakeResponseEntity.getResponseMessage;

@Slf4j
@RestController
@RequestMapping("rest/v1/auth")
public class AuthController {
    private final AuthService authService;

    private final PasswordEncoder passwordEncoder;


    public AuthController(AuthService authService, PasswordEncoder passwordEncoder) {
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/do")
    public ResponseEntity<?> auth(@RequestBody LoginDto loginInfo) {
        log.info("login info" + loginInfo.toString());

        if (loginInfo.getUserId() == null) {
            StatusEnum status = StatusEnum.BAD_REQUEST;
            String message = "아이디가 존재하지 않습니다.";
            return getResponseMessage(status, message);
        } else if (loginInfo.getPassword() == null) {
            StatusEnum status = StatusEnum.BAD_REQUEST;
            String message = "비밀번호가 존재하지 않습니다.";
            return getResponseMessage(status, message);
        }

        try {
            User loginUser = authService.auth(loginInfo);
            String userUuid = loginUser.getId();
            StatusEnum statusEnum = StatusEnum.OK;
            String message = "로그인 성공";
            return getResponseMessage(statusEnum, message, userUuid);
        } catch (NullPointerException nullPointerException) {
            StatusEnum statusEnum = StatusEnum.NOT_FOUND;
            String message = "해당 아이디는 존재하지 않습니다.";
            return getResponseMessage(statusEnum, message);
        } catch (IllegalAccessError illegalAccessError) {
            StatusEnum statusEnum = StatusEnum.BAD_REQUEST;
            String message = illegalAccessError.getMessage();
            return getResponseMessage(statusEnum, message);
        } catch (IllegalArgumentException illegalArgumentException) {
            StatusEnum statusEnum = StatusEnum.BAD_REQUEST;
            String message = "비밀번호가 일치하지 않습니다.";
            return getResponseMessage(statusEnum, message);
        }
    }

    @PostMapping("/registration-user")
    public ResponseEntity<?> registration(@RequestBody RegistrationDto registrationDto) {
        log.info("registration info" + registrationDto.toString());

        User user = User.builder()
                .id(UUID.randomUUID().toString())
                .userId(registrationDto.getUserId())
                .password(passwordEncoder.encode(registrationDto.getPassword()))
                .name(registrationDto.getName())
                .birth(registrationDto.getBirth())
                .phoneNumber(registrationDto.getPhoneNumber())
                .agreeTerm(registrationDto.isAgreeTerm())
                .build();

        if (user.getId() == null || user.getPassword() == null || user.getName() == null || user.getBirth() == null
                || user.getPhoneNumber() == null) {
            StatusEnum statusEnum = StatusEnum.BAD_REQUEST;
            String message = "필드중 null인 값이 존재 합니다." + user;
            return getResponseMessage(statusEnum, message);
        }

        if (!user.isAgreeTerm()) {
            StatusEnum statusEnum = StatusEnum.BAD_REQUEST;
            String message = "약관 동의를 하지 않아 가입이 불가능 합니다.";
            return getResponseMessage(statusEnum, message);
        }
        try {
            authService.registration(user);
            StatusEnum statusEnum = StatusEnum.OK;
            String message = "회원가입 성공";
            String userUuid = user.getId();
            return getResponseMessage(statusEnum, message, userUuid);
        } catch (DuplicateRequestException duplicateRequestException) {
            StatusEnum statusEnum = StatusEnum.BAD_REQUEST;
            String message = duplicateRequestException.getMessage();
            return getResponseMessage(statusEnum, message);
        } catch (PropertyValueException propertyException) {
            StatusEnum statusEnum = StatusEnum.BAD_REQUEST;
            String message = "잘못된 형식을 가진 값이 들어오거나 null인 값이 있습니다. : " + propertyException.getMessage();
            return getResponseMessage(statusEnum, message);
        }
    }

    @GetMapping("/find/id")
    public ResponseEntity<?> findIdUsingPhoneNumber(String phoneNumber) {
        try {
            String userId = authService.findIdUsingPhoneNumber(phoneNumber);
            StatusEnum statusEnum = StatusEnum.OK;
            String message = "조회 성공";
            return getResponseMessage(statusEnum, message, userId);
        } catch (NullPointerException nullPointerException) {
            StatusEnum statusEnum = StatusEnum.BAD_REQUEST;
            String message = nullPointerException.getMessage();
            return getResponseMessage(statusEnum, message);
        }
    }

    @GetMapping("find/password")
    public ResponseEntity<?> findPasswordByPhoneNumberAndId(String phoneNumber, String userId) {
        try {
            String changePasswordSessionId = authService.findPasswordByPhoneNumberAndId(phoneNumber, userId);
            StatusEnum statusEnum = StatusEnum.OK;
            String message = "조회 성공";
            return getResponseMessage(statusEnum, message, changePasswordSessionId);
        } catch (NullPointerException nullPointerException) {
            StatusEnum statusEnum = StatusEnum.BAD_REQUEST;
            String message = nullPointerException.getMessage();
            return getResponseMessage(statusEnum, message);
        }
    }

    @PostMapping("alter/password")
    public ResponseEntity<?> findPasswordByPhoneNumberAndId(@RequestBody ChangePasswordDto changePassword) {
        try {
            authService.alterPassword(changePassword.getChangeUUID(), changePassword.getChangePassword(), changePassword.getUserId(), changePassword.getPhoneNumber());
            StatusEnum statusEnum = StatusEnum.BAD_REQUEST;
            String message = "변경성공";
            return getResponseMessage(statusEnum, message);
        } catch (NullPointerException nullPointerException) {
            StatusEnum statusEnum = StatusEnum.BAD_REQUEST;
            String message = nullPointerException.getMessage();
            return getResponseMessage(statusEnum, message);
        }
    }
}
