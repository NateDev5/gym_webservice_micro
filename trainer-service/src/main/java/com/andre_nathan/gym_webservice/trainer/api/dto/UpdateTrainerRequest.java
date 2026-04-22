package com.andre_nathan.gym_webservice.trainer.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UpdateTrainerRequest(
        @NotBlank String fullName,
        @NotBlank @Email String email,
        @NotBlank String specialty,
        @NotNull Boolean active,
        @Valid @NotEmpty List<CreateTrainerRequest.CertificationRequest> certifications
) {
}
