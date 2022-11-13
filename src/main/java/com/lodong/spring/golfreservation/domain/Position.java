package com.lodong.spring.golfreservation.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter @ToString @Builder
@AllArgsConstructor @NoArgsConstructor

public class Position {
    @Id
    private int id;
    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "position")
    private List<PositionLock> positionLockList = new ArrayList<>();

    @PrePersist
    public void prePersist() {

    }
}
