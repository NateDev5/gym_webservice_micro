package com.andre_nathan.gym_webservice.enrollment.application.service;

import com.andre_nathan.gym_webservice.enrollment.application.exception.EnrollmentNotFoundException;
import com.andre_nathan.gym_webservice.enrollment.application.port.out.EnrollmentRepositoryPort;
import com.andre_nathan.gym_webservice.enrollment.domain.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentCrudServiceTest {

    @Mock
    private EnrollmentRepositoryPort repo;

    @InjectMocks
    private EnrollmentCrudService service;

    @Test
    void createSuccess() {
        when(repo.save(any(Enrollment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Enrollment created = service.create("member-1");

        assertEquals("member-1", created.getMemberId());
        verify(repo).save(any(Enrollment.class));
    }

    @Test
    void getByIdMissingThrowsNotFound() {
        when(repo.findById(EnrollmentId.of("missing"))).thenReturn(Optional.empty());

        assertThrows(EnrollmentNotFoundException.class, () -> service.getById("missing"));
    }

    @Test
    void updateMissingThrowsNotFound() {
        when(repo.findById(EnrollmentId.of("missing"))).thenReturn(Optional.empty());

        assertThrows(EnrollmentNotFoundException.class, () -> service.update(
                "missing",
                "member-1",
                List.of()
        ));
        verify(repo, never()).save(any(Enrollment.class));
    }

    @Test
    void getAllForScheduleFiltersByRegisteredClasses() {
        Enrollment match = enrollment("enrollment-1", "member-1",
                List.of(item("session-1", "trainer-1", "schedule-1")));
        Enrollment nonMatch = enrollment("enrollment-2", "member-2",
                List.of(item("session-2", "trainer-2", "schedule-2")));
        when(repo.findAll()).thenReturn(List.of(match, nonMatch));

        List<Enrollment> result = service.getAllForSchedule("schedule-1");

        assertEquals(1, result.size());
        assertEquals("member-1", result.get(0).getMemberId());
    }

    @Test
    void getAllForTrainerFiltersByRegisteredClasses() {
        Enrollment match = enrollment("enrollment-1", "member-1",
                List.of(item("session-1", "trainer-1", "schedule-1")));
        Enrollment nonMatch = enrollment("enrollment-2", "member-2",
                List.of(item("session-2", "trainer-2", "schedule-2")));
        when(repo.findAll()).thenReturn(List.of(match, nonMatch));

        List<Enrollment> result = service.getAllForTrainer("trainer-1");

        assertEquals(1, result.size());
        assertEquals("member-1", result.get(0).getMemberId());
    }

    @Test
    void deleteSuccess() {
        Enrollment existing = enrollment("enrollment-1", "member-1", List.of());
        when(repo.findById(EnrollmentId.of("enrollment-1"))).thenReturn(Optional.of(existing));

        service.delete("enrollment-1");

        verify(repo).deleteById(EnrollmentId.of("enrollment-1"));
    }

    private Enrollment enrollment(String enrollmentId, String memberId, List<EnrollmentItem> items) {
        return new Enrollment(EnrollmentId.of(enrollmentId), memberId, items);
    }

    private EnrollmentItem item(String classSessionId, String trainerId, String scheduleId) {
        return new EnrollmentItem(
                UUID.randomUUID(),
                EnrollmentDate.of(LocalDateTime.of(2026, 1, 1, 10, 0)),
                EnrollmentStatus.ENROLLED,
                ClassSessionId.of(classSessionId),
                trainerId,
                scheduleId,
                1
        );
    }
}
