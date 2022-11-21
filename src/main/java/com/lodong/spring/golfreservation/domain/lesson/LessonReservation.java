package com.lodong.spring.golfreservation.domain.lesson;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lodong.spring.golfreservation.domain.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @ToString @Builder
@AllArgsConstructor @NoArgsConstructor
public class LessonReservation {
    @Id
    private String id;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDate date;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "time")
    private InstructorTime time;

    @Column(nullable = false)
    private LocalDateTime createAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "instructor_id")
    private Instructor instructor;

    @PrePersist
    public void prePersist() {

    }

}
