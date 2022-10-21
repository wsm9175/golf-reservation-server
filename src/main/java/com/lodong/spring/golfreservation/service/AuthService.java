package com.lodong.spring.golfreservation.service;

import com.lodong.spring.golfreservation.domain.User;
import com.lodong.spring.golfreservation.dto.LoginDto;
import com.lodong.spring.golfreservation.repository.UserRepository;
import com.sun.jdi.request.DuplicateRequestException;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.PropertyValueException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    public User auth(LoginDto loginDto) throws NullPointerException, IllegalStateException{
        String id = loginDto.getUserId();
        String password = loginDto.getPassword();

        User user = userRepository.findByUserId(id).orElseThrow(NullPointerException::new);

        if(!user.getPassword().equals(password)){
            throw new IllegalAccessError("비밀번호가 불일치 합니다.");
        }
        return user;
    }

    @Transactional
    public void registration(User user) throws DuplicateRequestException, PropertyValueException {
        if(userRepository.existsByUserId(user.getUserId())){
            log.info("이미 존재하는 아이디입니다.");
            throw new DuplicateRequestException("이미 존재하는 아이디입니다.");
        }else if(userRepository.existsByPhoneNumber(user.getPhoneNumber())){
            log.info("이미 존재하는 전화번호입니다.");
            throw new DuplicateRequestException("이미 존재하는 전화번호입니다.");
        }
        userRepository.save(user);
    }

}
