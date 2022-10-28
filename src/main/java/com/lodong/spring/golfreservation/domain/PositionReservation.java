package com.lodong.spring.golfreservation.domain;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        uniqueConstraints={
                @UniqueConstraint(
                        name = "date",
                        columnNames={"date", "time","positionId"}
                )
        }
)
@Getter @Setter @ToString @Builder
@AllArgsConstructor @NoArgsConstructor
public class PositionReservation {
    @Id
    private String id;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "time")
    private Timetable time;

    @Column(nullable = false)
    private LocalDateTime createAt;

    @Column(nullable = false)
    private int positionId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User userId;
}
