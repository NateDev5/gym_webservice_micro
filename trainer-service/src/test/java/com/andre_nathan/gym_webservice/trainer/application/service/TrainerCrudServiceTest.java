package com.andre_nathan.gym_webservice.trainer.application.service;

import com.andre_nathan.gym_webservice.trainer.api.dto.CreateTrainerRequest;
import com.andre_nathan.gym_webservice.trainer.application.exception.DuplicateTrainerException;
import com.andre_nathan.gym_webservice.trainer.application.exception.TrainerNotFoundException;
import com.andre_nathan.gym_webservice.trainer.application.port.out.TrainerRepositoryPort;
import com.andre_nathan.gym_webservice.trainer.domain.model.FullName;
import com.andre_nathan.gym_webservice.trainer.domain.model.Specialty;
import com.andre_nathan.gym_webservice.trainer.domain.model.Trainer;
import com.andre_nathan.gym_webservice.trainer.domain.model.TrainerCertification;
import com.andre_nathan.gym_webservice.trainer.domain.model.TrainerId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerCrudServiceTest {

    @Mock
    private TrainerRepositoryPort repository;

    @InjectMocks
    private TrainerCrudService service;

    @Test
    void createSuccess() {
        when(repository.existsByEmail("jamie@example.com")).thenReturn(false);
        when(repository.save(any(Trainer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trainer created = service.create(
                "Jamie Blake",
                "jamie@example.com",
                "Yoga",
                true,
                certifications()
        );

        assertEquals("jamie@example.com", created.getEmail());
        verify(repository).save(any(Trainer.class));
    }

    @Test
    void createDuplicateThrowsConflict() {
        when(repository.existsByEmail("jamie@example.com")).thenReturn(true);

        assertThrows(DuplicateTrainerException.class, () -> service.create(
                "Jamie Blake",
                "jamie@example.com",
                "Yoga",
                true,
                certifications()
        ));
        verify(repository, never()).save(any(Trainer.class));
    }

    @Test
    void getByIdMissingThrowsNotFound() {
        when(repository.findById(any(TrainerId.class))).thenReturn(Optional.empty());
        assertThrows(TrainerNotFoundException.class, () -> service.getById("missing"));
    }

    @Test
    void getAllReturnsResults() {
        when(repository.findAll()).thenReturn(List.of(existingTrainer("trainer-1", "a@example.com")));

        List<Trainer> trainers = service.getAll();

        assertEquals(1, trainers.size());
    }

    @Test
    void updateMissingThrowsNotFound() {
        when(repository.findById(any(TrainerId.class))).thenReturn(Optional.empty());

        assertThrows(TrainerNotFoundException.class, () -> service.update(
                "missing",
                "Jamie Blake",
                "jamie@example.com",
                "Yoga",
                true,
                certifications()
        ));
        verify(repository, never()).save(any(Trainer.class));
    }

    @Test
    void deleteSuccess() {
        when(repository.findById(any(TrainerId.class))).thenReturn(Optional.of(existingTrainer("trainer-1", "a@example.com")));

        service.delete("trainer-1");

        verify(repository).deleteById(argThat(id -> "trainer-1".equals(id.value())));
    }

    @Test
    void emptyCertificationsRejected() {
        when(repository.existsByEmail(anyString())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> service.create(
                "Jamie Blake",
                "jamie@example.com",
                "Yoga",
                true,
                List.of()
        ));
    }

    private List<CreateTrainerRequest.CertificationRequest> certifications() {
        return List.of(new CreateTrainerRequest.CertificationRequest(
                UUID.randomUUID(),
                "Yoga Instructor",
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2027, 1, 1)
        ));
    }

    private Trainer existingTrainer(String trainerId, String email) {
        return new Trainer(
                TrainerId.of(trainerId),
                FullName.of("Existing Trainer"),
                email,
                Specialty.of("Yoga"),
                true,
                List.of(new TrainerCertification(
                        UUID.randomUUID(),
                        "Yoga Instructor",
                        LocalDate.of(2024, 1, 1),
                        LocalDate.of(2027, 1, 1)
                ))
        );
    }
}
