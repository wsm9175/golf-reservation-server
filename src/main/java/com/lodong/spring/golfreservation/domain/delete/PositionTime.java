package com.lodong.spring.golfreservation.domain.delete;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter @ToString @Builder
@AllArgsConstructor @NoArgsConstructor
public class PositionTime {
    @Id
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "position_id")
    private Position position;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "time_id")
    private Timetable timeId;


}
