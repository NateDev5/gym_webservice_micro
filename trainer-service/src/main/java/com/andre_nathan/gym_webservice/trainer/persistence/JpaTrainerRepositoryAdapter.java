package com.andre_nathan.gym_webservice.trainer.persistence;

import com.andre_nathan.gym_webservice.trainer.application.port.out.TrainerRepositoryPort;
import com.andre_nathan.gym_webservice.trainer.domain.model.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class JpaTrainerRepositoryAdapter implements TrainerRepositoryPort {
    private final SpringDataTrainerRepository repository;

    public JpaTrainerRepositoryAdapter(SpringDataTrainerRepository repository) {
        this.repository = repository;
    }

    @Override
    public Trainer save(Trainer trainer) {
        repository.save(toEntity(trainer));
        return trainer;
    }

    @Override
    public Optional<Trainer> findById(TrainerId trainerId) {
        return repository.findById(trainerId.value()).map(this::toDomain);
    }

    @Override
    public List<Trainer> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public boolean existsByEmailExcludingId(String email, TrainerId trainerId) {
        return repository.existsByEmailAndTrainerIdNot(email, trainerId.value());
    }

    @Override
    public void deleteById(TrainerId trainerId) {
        repository.deleteById(trainerId.value());
    }

    private TrainerJpaEntity toEntity(Trainer trainer) {
        TrainerJpaEntity entity = new TrainerJpaEntity();
        entity.trainerId = trainer.getTrainerId().value();
        entity.fullName = trainer.getFullName().value();
        entity.email = trainer.getEmail();
        entity.specialty = trainer.getSpecialty().value();
        entity.active = trainer.isActive();
        entity.certifications.clear();

        for (TrainerCertification certification : trainer.getCertifications()) {
            TrainerCertificationJpaEntity certificationEntity = new TrainerCertificationJpaEntity();
            certificationEntity.certificationId = certification.getCertificationId();
            certificationEntity.certificateName = certification.getCertificateName();
            certificationEntity.issuedDate = certification.getIssuedDate();
            certificationEntity.expiryDate = certification.getExpiryDate();
            certificationEntity.trainer = entity;
            entity.certifications.add(certificationEntity);
        }

        return entity;
    }

    private Trainer toDomain(TrainerJpaEntity entity) {
        return new Trainer(
                TrainerId.of(entity.trainerId),
                FullName.of(entity.fullName),
                entity.email,
                Specialty.of(entity.specialty),
                entity.active,
                entity.certifications.stream()
                        .map(certification -> new TrainerCertification(
                                certification.certificationId,
                                certification.certificateName,
                                certification.issuedDate,
                                certification.expiryDate
                        ))
                        .toList()
        );
    }
}
