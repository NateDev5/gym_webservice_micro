package com.andre_nathan.gym_webservice.enrollment.api.dto;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Relation(collectionRelation = "enrollments", itemRelation = "enrollment")
public class EnrollmentResponse extends RepresentationModel<EnrollmentResponse> {
    private final String enrollmentId;
    private final String memberId;
    private final String memberName;
    private final String membershipStatus;
    private final List<EnrollmentItemResponse> registeredClasses;

    public EnrollmentResponse(
            String enrollmentId,
            String memberId,
            String memberName,
            String membershipStatus,
            List<EnrollmentItemResponse> registeredClasses
    ) {
        this.enrollmentId = enrollmentId;
        this.memberId = memberId;
        this.memberName = memberName;
        this.membershipStatus = membershipStatus;
        this.registeredClasses = registeredClasses;
    }

    public String getEnrollmentId() {
        return enrollmentId;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getMembershipStatus() {
        return membershipStatus;
    }

    public List<EnrollmentItemResponse> getRegisteredClasses() {
        return registeredClasses;
    }

    public record EnrollmentItemResponse(
            UUID registrationId,
            LocalDateTime enrollmentDate,
            String enrollmentStatus,
            String classSessionId,
            String scheduleId,
            String className,
            String classType,
            String trainerId,
            String trainerName,
            String sessionStatus,
            LocalDateTime startTime,
            LocalDateTime endTime,
            String roomId,
            String roomName,
            Integer seatNumber
    ) {
    }
}
