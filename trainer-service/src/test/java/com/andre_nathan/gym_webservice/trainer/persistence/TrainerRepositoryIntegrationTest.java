package com.andre_nathan.gym_webservice.trainer.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("testing")
class TrainerRepositoryIntegrationTest {

    @Autowired
    private SpringDataTrainerRepository trainerRepository;

    @BeforeEach
    void setUp() {
        trainerRepository.deleteAll();
    }

    @Test
    void savesAndFindsById() {
        TrainerJpaEntity saved = trainerRepository.save(newTrainer("trainer-1", "trainer.one@example.com"));

        assertTrue(trainerRepository.findById(saved.trainerId).isPresent());
    }

    @Test
    void findsByEmailAndExistsQueries() {
        trainerRepository.save(newTrainer("trainer-2", "trainer.two@example.com"));

        assertTrue(trainerRepository.existsByEmail("trainer.two@example.com"));
        assertFalse(trainerRepository.existsByEmail("missing@example.com"));
        assertTrue(trainerRepository.existsByEmailAndTrainerIdNot("trainer.two@example.com", "other-id"));
        assertFalse(trainerRepository.existsByEmailAndTrainerIdNot("trainer.two@example.com", "trainer-2"));
    }

    @Test
    void deleteAndMissingReturnEmptyOrFalse() {
        trainerRepository.save(newTrainer("trainer-3", "trainer.three@example.com"));

        trainerRepository.deleteById("trainer-3");

        assertTrue(trainerRepository.findById("trainer-3").isEmpty());
        assertFalse(trainerRepository.existsByEmail("trainer.three@example.com"));
    }

    private TrainerJpaEntity newTrainer(String trainerId, String email) {
        TrainerJpaEntity trainer = new TrainerJpaEntity();
        trainer.trainerId = trainerId;
        trainer.fullName = "Test Trainer";
        trainer.email = email;
        trainer.specialty = "Yoga";
        trainer.active = true;

        TrainerCertificationJpaEntity certification = new TrainerCertificationJpaEntity();
        certification.certificationId = UUID.randomUUID();
        certification.certificateName = "Yoga Instructor";
        certification.issuedDate = LocalDate.of(2024, 1, 1);
        certification.expiryDate = LocalDate.of(2027, 1, 1);
        certification.trainer = trainer;
        trainer.certifications.add(certification);

        return trainer;
    }
}
