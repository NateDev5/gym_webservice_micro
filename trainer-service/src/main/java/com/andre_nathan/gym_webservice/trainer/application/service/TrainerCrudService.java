package com.andre_nathan.gym_webservice.trainer.application.service;

import com.andre_nathan.gym_webservice.trainer.api.dto.CreateTrainerRequest;
import com.andre_nathan.gym_webservice.trainer.application.exception.DuplicateTrainerException;
import com.andre_nathan.gym_webservice.trainer.application.exception.TrainerNotFoundException;
import com.andre_nathan.gym_webservice.trainer.application.port.out.TrainerRepositoryPort;
import com.andre_nathan.gym_webservice.trainer.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TrainerCrudService {
    private final TrainerRepositoryPort repository;

    public TrainerCrudService(TrainerRepositoryPort repository) {
        this.repository = repository;
    }

    @Transactional
    public Trainer create(
            String fullName,
            String email,
            String specialty,
            boolean active,
            List<CreateTrainerRequest.CertificationRequest> certifications
    ) {
        if (repository.existsByEmail(email)) {
            throw new DuplicateTrainerException(email);
        }

        Trainer trainer = new Trainer(
                TrainerId.newId(),
                FullName.of(fullName),
                email,
                Specialty.of(specialty),
                active,
                toCertifications(certifications)
        );
        return repository.save(trainer);
    }

    @Transactional
    public Trainer update(
            String trainerId,
            String fullName,
            String email,
            String specialty,
            boolean active,
            List<CreateTrainerRequest.CertificationRequest> certifications
    ) {
        TrainerId parsedTrainerId = TrainerId.of(trainerId);
        getById(parsedTrainerId);

        if (repository.existsByEmailExcludingId(email, parsedTrainerId)) {
            throw new DuplicateTrainerException(email);
        }

        Trainer trainer = new Trainer(
                parsedTrainerId,
                FullName.of(fullName),
                email,
                Specialty.of(specialty),
                active,
                toCertifications(certifications)
        );
        return repository.save(trainer);
    }

    @Transactional(readOnly = true)
    public Trainer getById(String trainerId) {
        return getById(TrainerId.of(trainerId));
    }

    @Transactional(readOnly = true)
    public List<Trainer> getAll() {
        return repository.findAll();
    }

    @Transactional
    public void delete(String trainerId) {
        TrainerId parsedTrainerId = TrainerId.of(trainerId);
        getById(parsedTrainerId);
        repository.deleteById(parsedTrainerId);
    }

    private Trainer getById(TrainerId trainerId) {
        return repository.findById(trainerId)
                .orElseThrow(() -> new TrainerNotFoundException(trainerId));
    }

    private List<TrainerCertification> toCertifications(List<CreateTrainerRequest.CertificationRequest> certifications) {
        if (certifications == null || certifications.isEmpty()) {
            throw new IllegalArgumentException("certifications cannot be empty");
        }

        return certifications.stream()
                .map(request -> new TrainerCertification(
                        request.certificationId() == null ? UUID.randomUUID() : request.certificationId(),
                        request.certificateName(),
                        request.issuedDate(),
                        request.expiryDate()
                ))
                .toList();
    }
}
