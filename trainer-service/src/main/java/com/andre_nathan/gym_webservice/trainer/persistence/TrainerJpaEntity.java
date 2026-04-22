package com.andre_nathan.gym_webservice.trainer.persistence;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trainers")
public class TrainerJpaEntity {
    @Id
    @Column(name = "trainer_id", nullable = false, updatable = false)
    public String trainerId;

    @Column(name = "full_name", nullable = false)
    public String fullName;

    @Column(name = "email", nullable = false, unique = true)
    public String email;

    @Column(name = "specialty", nullable = false)
    public String specialty;

    @Column(name = "active", nullable = false)
    public boolean active;

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    public List<TrainerCertificationJpaEntity> certifications = new ArrayList<>();

    protected TrainerJpaEntity() {
    }
}
