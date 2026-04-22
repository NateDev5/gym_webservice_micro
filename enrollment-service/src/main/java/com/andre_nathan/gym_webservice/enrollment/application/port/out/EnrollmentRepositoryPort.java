package com.andre_nathan.gym_webservice.enrollment.application.port.out;

import com.andre_nathan.gym_webservice.enrollment.domain.model.Enrollment;
import com.andre_nathan.gym_webservice.enrollment.domain.model.EnrollmentId;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepositoryPort {
    Enrollment save(Enrollment enrollment);
    Optional<Enrollment> findById(EnrollmentId id);
    boolean existsById(EnrollmentId id);
    List<Enrollment> findAll();
    List<Enrollment> findAllForMember(String memberId);
    void deleteById(EnrollmentId id);
}
