package com.andre_nathan.gym_webservice.enrollment.infrastructure.persistence;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "enrollment_items")
public class EnrollmentItemJpaEntity {
    @Id
    @Column(name = "registration_id", nullable = false, updatable = false)
    public UUID registrationId;

    @Column(name = "enrollment_date", nullable = false)
    public LocalDateTime enrollmentDate;

    @Column(name = "enrollment_status", nullable = false)
    public String enrollmentStatus;

    @Column(name = "class_session_id", nullable = false)
    public String classSessionId;

    @Column(name = "trainer_id", nullable = false)
    public String trainerId;

    @Column(name = "schedule_id", nullable = false)
    public String scheduleId;

    @Column(name = "seat_number")
    public Integer seatNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false)
    public EnrollmentJpaEntity enrollment;

    protected EnrollmentItemJpaEntity() {
    }
}
