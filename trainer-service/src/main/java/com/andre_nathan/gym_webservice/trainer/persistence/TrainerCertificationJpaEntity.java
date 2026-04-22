package com.andre_nathan.gym_webservice.trainer.persistence;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "trainer_certifications")
public class TrainerCertificationJpaEntity {
    @Id
    @Column(name = "certification_id", nullable = false, updatable = false)
    public UUID certificationId;

    @Column(name = "certificate_name", nullable = false)
    public String certificateName;

    @Column(name = "issued_date", nullable = false)
    public LocalDate issuedDate;

    @Column(name = "expiry_date", nullable = false)
    public LocalDate expiryDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id", nullable = false)
    public TrainerJpaEntity trainer;

    protected TrainerCertificationJpaEntity() {
    }
}
