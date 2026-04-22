package com.andre_nathan.gym_webservice.enrollment.domain.model;

import java.util.Objects;
import java.util.UUID;

public class EnrollmentItem {
    private final UUID registrationId;
    private EnrollmentDate enrollmentDate;
    private EnrollmentStatus enrollmentStatus;
    private ClassSessionId classSessionId;
    private final String trainerId;
    private final String scheduleId;
    private Integer seatNumber;

    public EnrollmentItem(
            UUID registrationId,
            EnrollmentDate enrollmentDate,
            EnrollmentStatus enrollmentStatus,
            ClassSessionId classSessionId,
            String trainerId,
            String scheduleId,
            Integer seatNumber
    ) {
        this.registrationId = Objects.requireNonNull(registrationId, "registrationId cannot be null");
        this.enrollmentDate = Objects.requireNonNull(enrollmentDate, "enrollmentDate cannot be null");
        this.enrollmentStatus = Objects.requireNonNull(enrollmentStatus, "enrollmentStatus cannot be null");
        this.classSessionId = Objects.requireNonNull(classSessionId, "classSessionId cannot be null");
        this.trainerId = Objects.requireNonNull(trainerId, "trainerId cannot be null");
        this.scheduleId = Objects.requireNonNull(scheduleId, "scheduleId cannot be null");
        this.seatNumber = seatNumber;
    }

    public UUID getRegistrationId() {
        return registrationId;
    }

    public EnrollmentDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public EnrollmentStatus getEnrollmentStatus() {
        return enrollmentStatus;
    }

    public ClassSessionId getClassSessionId() {
        return classSessionId;
    }

    public Integer getSeatNumber() {
        return seatNumber;
    }

    public String getTrainerId() {
        return trainerId;
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public boolean isCancelled() {
        return enrollmentStatus == EnrollmentStatus.CANCELLED;
    }

    public void cancel() {
        this.enrollmentStatus = EnrollmentStatus.CANCELLED;
    }
}
