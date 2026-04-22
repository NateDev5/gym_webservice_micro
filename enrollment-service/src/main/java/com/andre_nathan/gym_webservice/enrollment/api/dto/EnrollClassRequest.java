package com.andre_nathan.gym_webservice.enrollment.api.dto;

import jakarta.validation.constraints.NotBlank;

public record EnrollClassRequest(
        @NotBlank String memberId,
        @NotBlank String classSessionId,
        @NotBlank String trainerId,
        @NotBlank String scheduleId
) {
}
