package com.lodong.spring.golfreservation.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter @ToString @Builder
@AllArgsConstructor @NoArgsConstructor

public class Position {
    @Id
    private int id;

    @PrePersist
    public void prePersist() {

    }
}
