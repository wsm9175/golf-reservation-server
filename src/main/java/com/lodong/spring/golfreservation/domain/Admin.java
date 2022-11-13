package com.lodong.spring.golfreservation.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter @ToString @Builder
@AllArgsConstructor @NoArgsConstructor

public class Admin {
    @Id
    private String id;

    @Column(nullable = false)
    private String adminId;
    @Column(nullable = false)
    private String password;
}
