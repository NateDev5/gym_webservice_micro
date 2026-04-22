package com.andre_nathan.gym_webservice.enrollment.application.service;

import com.andre_nathan.gym_webservice.enrollment.application.exception.EnrollmentNotFoundException;
import com.andre_nathan.gym_webservice.enrollment.application.port.out.EnrollmentRepositoryPort;
import com.andre_nathan.gym_webservice.enrollment.domain.model.*;
import com.andre_nathan.gym_webservice.member.domain.model.MemberId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class EnrollmentCrudService {
    private final EnrollmentRepositoryPort repo;

    public EnrollmentCrudService(EnrollmentRepositoryPort repo) {
        this.repo = repo;
    }

    @Transactional
    public Enrollment create(String memberId) {
        MemberId parsedMemberId = MemberId.of(requireText(memberId, "memberId"));

        Enrollment enrollment = new Enrollment(
                EnrollmentId.newId(),
                parsedMemberId,
                List.of()
        );

        return repo.save(enrollment);
    }

    @Transactional
    public Enrollment update(
            String enrollmentId,
            String memberId,
            List<EnrollmentItem> registeredClasses
    ) {
        EnrollmentId parsedEnrollmentId = EnrollmentId.of(requireText(enrollmentId, "enrollmentId"));
        MemberId parsedMemberId = MemberId.of(requireText(memberId, "memberId"));

        getById(parsedEnrollmentId);

        Enrollment updatedEnrollment = new Enrollment(
                parsedEnrollmentId,
                parsedMemberId,
                requireNonNull(registeredClasses, "registeredClasses")
        );

        return repo.save(updatedEnrollment);
    }

    @Transactional(readOnly = true)
    public Enrollment getById(String enrollmentId) {
        EnrollmentId parsedEnrollmentId = EnrollmentId.of(requireText(enrollmentId, "enrollmentId"));
        return getById(parsedEnrollmentId);
    }

    @Transactional(readOnly = true)
    public List<Enrollment> getAll() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public List<Enrollment> getAllForMember(String memberId) {
        MemberId parsedMemberId = MemberId.of(requireText(memberId, "memberId"));
        return repo.findAllForMember(parsedMemberId);
    }

    @Transactional
    public void delete(String enrollmentId) {
        EnrollmentId parsedEnrollmentId = EnrollmentId.of(requireText(enrollmentId, "enrollmentId"));
        getById(parsedEnrollmentId);
        repo.deleteById(parsedEnrollmentId);
    }

    private Enrollment getById(EnrollmentId enrollmentId) {
        return repo.findById(enrollmentId)
                .orElseThrow(() -> new EnrollmentNotFoundException(enrollmentId));
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }

        return value.trim();
    }

    private <T> T requireNonNull(T value, String fieldName) {
        return Objects.requireNonNull(value, fieldName + " cannot be null");
    }
}
