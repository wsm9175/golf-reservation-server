package com.lodong.spring.golfreservation.domain.lesson;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter @Setter @ToString
@Builder @AllArgsConstructor @NoArgsConstructor
public class LessonLock {
    @Id
    private String id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_id")
    private InstructorTime instructorTime;
    @Column(nullable = false)
    private LocalDate date;

}
