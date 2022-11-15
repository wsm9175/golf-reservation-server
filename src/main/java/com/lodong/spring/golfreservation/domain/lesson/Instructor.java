package com.lodong.spring.golfreservation.domain.lesson;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lodong.spring.golfreservation.domain.File;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class Instructor {
    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String info;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "profile_image_path")
    private File file;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "instructor")
    private List<InstructorTime> instructorTimeList = new ArrayList<>();

    @PrePersist
    public void prePersist(){

    }
}
