package com.lodong.spring.golfreservation.domain.lesson;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter @ToString
@Builder @AllArgsConstructor @NoArgsConstructor
public class InstructorTime {
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id")
    private Instructor instructor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_id")
    private LessonTimeTable lessonTimeTable;
}
