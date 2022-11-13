package com.lodong.spring.golfreservation.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter @ToString @Builder
@AllArgsConstructor @NoArgsConstructor
public class Holiday {
    @Id
    private String id;
    @Column
    private String name;

}
