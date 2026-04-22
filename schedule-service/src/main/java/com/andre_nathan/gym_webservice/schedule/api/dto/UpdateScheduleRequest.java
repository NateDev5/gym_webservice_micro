package com.andre_nathan.gym_webservice.schedule.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UpdateScheduleRequest(
        @NotBlank String className,
        @NotBlank String classType,
        @NotNull LocalDateTime startTime,
        @NotNull LocalDateTime endTime,
        @NotBlank String roomId,
        @NotBlank String roomName,
        @Min(1) int roomCapacity,
        @NotBlank String trainerId,
        @Min(1) int maxCapacity,
        @Min(0) int enrolledCount,
        @NotBlank String classSessionId,
        @NotNull LocalDate sessionDate,
        @NotBlank String sessionStatus
) {
}
