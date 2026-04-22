package com.andre_nathan.gym_webservice.enrollment.api.dto;

import jakarta.validation.constraints.NotBlank;

public record CancelEnrollmentRequest(
        @NotBlank String memberId,
        @NotBlank String classSessionId
) {
}
