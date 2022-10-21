package com.lodong.spring.golfreservation.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter @ToString @Builder
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

    @PrePersist
    public void prePersist(){

    }
}
