package com.andre_nathan.gym_webservice.schedule.application.service;

import com.andre_nathan.gym_webservice.schedule.application.exception.ClassSessionNotFoundException;
import com.andre_nathan.gym_webservice.schedule.application.exception.RoomBookingConflictException;
import com.andre_nathan.gym_webservice.schedule.application.exception.ScheduleNotFoundException;
import com.andre_nathan.gym_webservice.schedule.application.exception.TrainerScheduleConflictException;
import com.andre_nathan.gym_webservice.schedule.application.port.out.ScheduleRepositoryPort;
import com.andre_nathan.gym_webservice.schedule.domain.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleCrudServiceTest {

    @Mock
    private ScheduleRepositoryPort repository;

    @InjectMocks
    private ScheduleCrudService service;

    @Test
    void createSuccess() {
        when(repository.findAll()).thenReturn(List.of());
        when(repository.save(any(Schedule.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Schedule created = service.create(
                "Morning Yoga",
                "Yoga",
                LocalDateTime.of(2026, 8, 1, 9, 0),
                LocalDateTime.of(2026, 8, 1, 10, 0),
                "room-1",
                "Studio A",
                20,
                "trainer-1",
                20,
                1,
                "session-1",
                LocalDate.of(2026, 8, 1),
                "SCHEDULED"
        );

        assertEquals("Morning Yoga", created.getClassName());
        verify(repository).save(any(Schedule.class));
    }

    @Test
    void createRoomConflictThrowsConflict() {
        when(repository.findAll()).thenReturn(List.of(schedule(
                "existing",
                "existing-session",
                "room-1",
                "trainer-1",
                LocalDateTime.of(2026, 8, 1, 9, 0),
                LocalDateTime.of(2026, 8, 1, 10, 0),
                20,
                1
        )));

        assertThrows(RoomBookingConflictException.class, () -> service.create(
                "Morning Yoga",
                "Yoga",
                LocalDateTime.of(2026, 8, 1, 9, 15),
                LocalDateTime.of(2026, 8, 1, 10, 15),
                "room-1",
                "Studio A",
                20,
                "trainer-2",
                20,
                1,
                "session-2",
                LocalDate.of(2026, 8, 1),
                "SCHEDULED"
        ));
        verify(repository, never()).save(any(Schedule.class));
    }

    @Test
    void createTrainerConflictThrowsConflict() {
        when(repository.findAll()).thenReturn(List.of(schedule(
                "existing",
                "existing-session",
                "room-1",
                "trainer-1",
                LocalDateTime.of(2026, 8, 1, 9, 0),
                LocalDateTime.of(2026, 8, 1, 10, 0),
                20,
                1
        )));

        assertThrows(TrainerScheduleConflictException.class, () -> service.create(
                "Morning Yoga",
                "Yoga",
                LocalDateTime.of(2026, 8, 1, 9, 15),
                LocalDateTime.of(2026, 8, 1, 10, 15),
                "room-2",
                "Studio B",
                20,
                "trainer-1",
                20,
                1,
                "session-2",
                LocalDate.of(2026, 8, 1),
                "SCHEDULED"
        ));
        verify(repository, never()).save(any(Schedule.class));
    }

    @Test
    void getByIdMissingThrowsNotFound() {
        when(repository.findById(any(ScheduleId.class))).thenReturn(Optional.empty());
        assertThrows(ScheduleNotFoundException.class, () -> service.getById("missing"));
    }

    @Test
    void getByClassSessionMissingThrowsNotFound() {
        when(repository.findByClassSessionId("missing-session")).thenReturn(Optional.empty());
        assertThrows(ClassSessionNotFoundException.class, () -> service.getByClassSessionId("missing-session"));
    }

    @Test
    void reserveSeatSuccess() {
        Schedule existing = schedule(
                "schedule-1",
                "session-1",
                "room-1",
                "trainer-1",
                LocalDateTime.of(2026, 8, 1, 9, 0),
                LocalDateTime.of(2026, 8, 1, 10, 0),
                2,
                1
        );
        when(repository.findByClassSessionId("session-1")).thenReturn(Optional.of(existing));
        when(repository.save(any(Schedule.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Schedule updated = service.reserveSeat("session-1");

        assertEquals(2, updated.getEnrolledCount());
    }

    @Test
    void deleteSuccess() {
        when(repository.findById(any(ScheduleId.class))).thenReturn(Optional.of(schedule(
                "schedule-1",
                "session-1",
                "room-1",
                "trainer-1",
                LocalDateTime.of(2026, 8, 1, 9, 0),
                LocalDateTime.of(2026, 8, 1, 10, 0),
                20,
                1
        )));

        service.delete("schedule-1");

        verify(repository).deleteById(argThat(id -> "schedule-1".equals(id.value())));
    }

    private Schedule schedule(
            String scheduleId,
            String classSessionId,
            String roomId,
            String trainerId,
            LocalDateTime start,
            LocalDateTime end,
            int maxCapacity,
            int enrolledCount
    ) {
        return new Schedule(
                ScheduleId.of(scheduleId),
                "Morning Yoga",
                "Yoga",
                new TimeSlot(start, end),
                new Room(RoomId.of(roomId), "Studio", 30),
                trainerId,
                maxCapacity,
                enrolledCount,
                new ClassSession(ClassSessionId.of(classSessionId), start.toLocalDate(), "SCHEDULED")
        );
    }
}
