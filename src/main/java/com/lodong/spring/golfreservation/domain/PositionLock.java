package com.lodong.spring.golfreservation.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter @Setter @ToString @Builder
@AllArgsConstructor @NoArgsConstructor

public class PositionLock {
    @Id
    private String id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id")
    private Position position;
    @Column(nullable = false)
    private LocalDate date;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_id")
    private Timetable timetable;

}
