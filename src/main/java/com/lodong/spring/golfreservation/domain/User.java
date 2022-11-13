package com.lodong.spring.golfreservation.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import java.time.LocalDate;

@Entity
@Getter @Setter @ToString @Builder
@AllArgsConstructor @NoArgsConstructor
public class User {
    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate birth;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private boolean agreeTerm;

    @PrePersist
    public void prePersist() {

    }

}
