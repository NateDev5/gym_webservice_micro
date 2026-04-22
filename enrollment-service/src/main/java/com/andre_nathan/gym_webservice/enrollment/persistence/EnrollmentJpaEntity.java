package com.andre_nathan.gym_webservice.enrollment.persistence;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "enrollments")
public class EnrollmentJpaEntity {
    @Id
    @Column(name = "enrollment_id", nullable = false, updatable = false)
    public String enrollmentId;

    @Column(name = "member_id", nullable = false)
    public String memberId;

    @OneToMany(mappedBy = "enrollment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    public List<EnrollmentItemJpaEntity> registeredClasses = new ArrayList<>();

    protected EnrollmentJpaEntity() {
    }
}
