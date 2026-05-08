package com.andre_nathan.gym_webservice.trainer.domain.model;

import com.andre_nathan.gym_webservice.trainer.domain.exception.InvalidCertificationPeriodException;
import com.andre_nathan.gym_webservice.trainer.domain.exception.InvalidTrainerEmailException;
import com.andre_nathan.gym_webservice.trainer.domain.exception.InvalidTrainerNameException;
import com.andre_nathan.gym_webservice.trainer.domain.exception.InvalidTrainerSpecialtyException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TrainerDomainTest {

    @Test
    void createsValidTrainerAndSupportsCanTeach() {
        Trainer trainer = new Trainer(
                TrainerId.newId(),
                FullName.of("Jamie Blake"),
                "jamie.blake@example.com",
                Specialty.of("Yoga"),
                true,
                List.of(validCertification())
        );

        assertTrue(trainer.isActive());
        assertTrue(trainer.canTeach("Yoga"));
        assertTrue(trainer.canTeach("Power Yoga"));
    }

    @Test
    void rejectsInvalidTrainerValues() {
        assertThrows(InvalidTrainerNameException.class, () -> FullName.of("x"));
        assertThrows(InvalidTrainerSpecialtyException.class, () -> Specialty.of("x"));
        assertThrows(InvalidTrainerEmailException.class, () -> new Trainer(
                TrainerId.newId(),
                FullName.of("Jamie Blake"),
                "invalid-email",
                Specialty.of("Yoga"),
                true,
                List.of(validCertification())
        ));
    }

    @Test
    void rejectsInvalidCertificationPeriod() {
        assertThrows(InvalidCertificationPeriodException.class, () -> new TrainerCertification(
                UUID.randomUUID(),
                "Yoga Cert",
                LocalDate.of(2026, 1, 10),
                LocalDate.of(2026, 1, 10)
        ));
    }

    private TrainerCertification validCertification() {
        return new TrainerCertification(
                UUID.randomUUID(),
                "Yoga Cert",
                LocalDate.of(2024, 1, 10),
                LocalDate.of(2027, 1, 10)
        );
    }
}
