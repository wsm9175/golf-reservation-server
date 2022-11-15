package com.lodong.spring.golfreservation.domain.cancel;


import com.lodong.spring.golfreservation.domain.User;
import com.lodong.spring.golfreservation.domain.lesson.InstructorTime;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter @Setter @ToString @Builder
@AllArgsConstructor @NoArgsConstructor

public class CancelReservationLesson {
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "time_id")
    private InstructorTime instructorTime;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalDate createAt;
}
