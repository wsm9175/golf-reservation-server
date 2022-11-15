package com.lodong.spring.golfreservation.domain.lesson;

import lombok.*;

import javax.persistence.*;
import java.time.LocalTime;

@Entity
@Getter @Setter @ToString @Builder
@AllArgsConstructor @NoArgsConstructor
public class LessonTimeTable {
    @Id
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;
}
