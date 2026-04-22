package com.andre_nathan.gym_webservice.trainer.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CreateTrainerRequest(
        @NotBlank String fullName,
        @NotBlank @Email String email,
        @NotBlank String specialty,
        @NotNull Boolean active,
        @Valid @NotEmpty List<CertificationRequest> certifications
) {
    public record CertificationRequest(
            UUID certificationId,
            @NotBlank String certificateName,
            @NotNull LocalDate issuedDate,
            @NotNull LocalDate expiryDate
    ) {
    }
}
