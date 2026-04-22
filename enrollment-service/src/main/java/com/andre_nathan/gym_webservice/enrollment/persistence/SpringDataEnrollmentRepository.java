package com.andre_nathan.gym_webservice.enrollment.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataEnrollmentRepository extends JpaRepository<EnrollmentJpaEntity, String> {
    List<EnrollmentJpaEntity> findAllByMemberId(String memberId);
}
