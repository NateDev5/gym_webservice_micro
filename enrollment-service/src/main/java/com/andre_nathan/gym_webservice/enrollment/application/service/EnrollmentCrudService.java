package com.andre_nathan.gym_webservice.enrollment.application.service;

import com.andre_nathan.gym_webservice.enrollment.application.exception.EnrollmentNotFoundException;
import com.andre_nathan.gym_webservice.enrollment.application.port.out.EnrollmentRepositoryPort;
import com.andre_nathan.gym_webservice.enrollment.domain.model.*;
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
        String parsedMemberId = requireText(memberId, "memberId");

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
        String parsedMemberId = requireText(memberId, "memberId");

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
        String parsedMemberId = requireText(memberId, "memberId");
        return repo.findAllForMember(parsedMemberId);
    }

    @Transactional(readOnly = true)
    public List<Enrollment> getAllForSchedule(String scheduleId) {
        String parsedScheduleId = requireText(scheduleId, "scheduleId");
        return repo.findAll().stream()
                .filter(enrollment -> enrollment.getRegisteredClasses().stream()
                        .anyMatch(item -> parsedScheduleId.equals(item.getScheduleId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Enrollment> getAllForTrainer(String trainerId) {
        String parsedTrainerId = requireText(trainerId, "trainerId");
        return repo.findAll().stream()
                .filter(enrollment -> enrollment.getRegisteredClasses().stream()
                        .anyMatch(item -> parsedTrainerId.equals(item.getTrainerId())))
                .toList();
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
