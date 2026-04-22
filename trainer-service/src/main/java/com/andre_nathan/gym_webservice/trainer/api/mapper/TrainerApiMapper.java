package com.andre_nathan.gym_webservice.trainer.api.mapper;

import com.andre_nathan.gym_webservice.trainer.api.dto.TrainerResponse;
import com.andre_nathan.gym_webservice.trainer.domain.model.Trainer;

public final class TrainerApiMapper {
    private TrainerApiMapper() {
    }

    public static TrainerResponse toResponse(Trainer trainer) {
        return new TrainerResponse(
                trainer.getTrainerId().value(),
                trainer.getFullName().value(),
                trainer.getEmail(),
                trainer.getSpecialty().value(),
                trainer.isActive(),
                trainer.getCertifications().stream()
                        .map(certification -> new TrainerResponse.CertificationResponse(
                                certification.getCertificationId(),
                                certification.getCertificateName(),
                                certification.getIssuedDate(),
                                certification.getExpiryDate()
                        ))
                        .toList()
        );
    }
}
