package com.andre_nathan.gym_webservice.trainer.api.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record TrainerResponse(
        String trainerId,
        String fullName,
        String email,
        String specialty,
        boolean active,
        List<CertificationResponse> certifications
) {
    public record CertificationResponse(
            UUID certificationId,
            String certificateName,
            LocalDate issuedDate,
            LocalDate expiryDate
    ) {
    }
}
