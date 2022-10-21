package com.lodong.spring.golfreservation.domain;

import lombok.*;

import javax.persistence.*;
import java.sql.Time;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Getter @Setter @ToString @Builder
@AllArgsConstructor @NoArgsConstructor
public class CancelReservation {
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "time")
    private Timetable time;

    @Column(nullable = false)
    private int positionId;

    @Column(nullable = false)
    private LocalDate createAt;

    @PrePersist
    public void prePersist() {

    }
}
