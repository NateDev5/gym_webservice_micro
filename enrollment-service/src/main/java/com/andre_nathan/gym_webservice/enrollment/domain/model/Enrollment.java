package com.andre_nathan.gym_webservice.enrollment.domain.model;

import com.andre_nathan.gym_webservice.enrollment.domain.exception.AlreadyRegisteredException;
import com.andre_nathan.gym_webservice.enrollment.domain.exception.EnrollmentRecordNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Enrollment {
    private final EnrollmentId enrollmentId;
    private final String memberId;
    private List<EnrollmentItem> registeredClasses;

    public Enrollment(
            EnrollmentId enrollmentId,
            String memberId,
            List<EnrollmentItem> registeredClasses
    ) {
        this.enrollmentId = Objects.requireNonNull(enrollmentId, "enrollmentId cannot be null");
        this.memberId = requireText(memberId, "memberId");
        this.registeredClasses = List.copyOf(Objects.requireNonNull(registeredClasses, "registeredClasses cannot be null"));
    }

    public EnrollmentId getEnrollmentId() {
        return enrollmentId;
    }

    public String getMemberId() {
        return memberId;
    }

    public List<EnrollmentItem> getRegisteredClasses() {
        return registeredClasses;
    }

    public boolean hasRegisteredClasses() {
        return !registeredClasses.isEmpty();
    }

    public void enroll(ClassSessionId classSessionId, String trainerId, String scheduleId, Integer seatNumber) {
        // Membership validity must be checked outside this aggregate.
        Objects.requireNonNull(classSessionId, "classSessionId cannot be null");
        Objects.requireNonNull(trainerId, "trainerId cannot be null");
        Objects.requireNonNull(scheduleId, "scheduleId cannot be null");

        boolean alreadyRegistered = registeredClasses.stream()
                .anyMatch(item -> item.getClassSessionId().equals(classSessionId)
                        && item.getEnrollmentStatus() != EnrollmentStatus.CANCELLED);

        if (alreadyRegistered) {
            throw new AlreadyRegisteredException();
        }

        List<EnrollmentItem> updatedRegisteredClasses = new ArrayList<>(registeredClasses);
        updatedRegisteredClasses.add(
                new EnrollmentItem(
                        UUID.randomUUID(),
                        EnrollmentDate.now(),
                        EnrollmentStatus.ENROLLED,
                        classSessionId,
                        trainerId,
                        scheduleId,
                        seatNumber
                )
        );
        this.registeredClasses = List.copyOf(updatedRegisteredClasses);
    }

    public EnrollmentItem getRegistrationFor(ClassSessionId classSessionId) {
        return registeredClasses.stream()
                .filter(item -> item.getClassSessionId().equals(classSessionId))
                .findFirst()
                .orElseThrow(() -> new EnrollmentRecordNotFoundException(classSessionId.value()));
    }

    public void cancelEnrollment(ClassSessionId classSessionId) {
        Objects.requireNonNull(classSessionId, "classSessionId cannot be null");

        List<EnrollmentItem> updatedRegisteredClasses = new ArrayList<>(registeredClasses);
        EnrollmentItem item = updatedRegisteredClasses.stream()
                .filter(existingItem -> existingItem.getClassSessionId().equals(classSessionId))
                .findFirst()
                .orElseThrow(() -> new EnrollmentRecordNotFoundException(classSessionId.value()));

        item.cancel();

        this.registeredClasses = List.copyOf(updatedRegisteredClasses);
    }

    private String requireText(String value, String fieldName) {
        String normalizedValue = Objects.requireNonNull(value, fieldName + " cannot be null").trim();
        if (normalizedValue.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }
        return normalizedValue;
    }
}
