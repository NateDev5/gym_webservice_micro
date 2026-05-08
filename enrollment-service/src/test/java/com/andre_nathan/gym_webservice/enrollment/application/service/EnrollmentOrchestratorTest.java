package com.andre_nathan.gym_webservice.enrollment.application.service;

import com.andre_nathan.gym_webservice.enrollment.application.exception.ClassSessionNotFoundException;
import com.andre_nathan.gym_webservice.enrollment.application.exception.InactiveTrainerException;
import com.andre_nathan.gym_webservice.enrollment.application.exception.InvalidMembershipException;
import com.andre_nathan.gym_webservice.enrollment.application.port.out.EnrollmentRepositoryPort;
import com.andre_nathan.gym_webservice.enrollment.application.port.out.MemberPort;
import com.andre_nathan.gym_webservice.enrollment.application.port.out.SchedulePort;
import com.andre_nathan.gym_webservice.enrollment.application.port.out.TrainerPort;
import com.andre_nathan.gym_webservice.enrollment.domain.exception.AlreadyRegisteredException;
import com.andre_nathan.gym_webservice.enrollment.domain.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentOrchestratorTest {

    @Mock
    private EnrollmentRepositoryPort enrollmentRepository;
    @Mock
    private MemberPort memberPort;
    @Mock
    private TrainerPort trainerPort;
    @Mock
    private SchedulePort schedulePort;

    @InjectMocks
    private EnrollmentOrchestrator orchestrator;

    @Test
    void successfulOrchestration() {
        when(memberPort.findById("member-1")).thenReturn(Optional.of(activeMember("member-1")));
        when(schedulePort.findByClassSessionId("session-1")).thenReturn(Optional.of(schedule("schedule-1", "session-1", "trainer-1", "Yoga", 10, 0)));
        when(trainerPort.findById("trainer-1")).thenReturn(Optional.of(activeTrainer("trainer-1", "Yoga")));
        when(enrollmentRepository.findAllForMember("member-1")).thenReturn(List.of(enrollment("enrollment-1", "member-1", List.of())));
        when(schedulePort.save(any(SchedulePort.ScheduleSnapshot.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Enrollment result = orchestrator.enrollMemberInClass("member-1", "session-1", "trainer-1", "schedule-1");

        assertEquals(1, result.getRegisteredClasses().size());
        verify(schedulePort).save(any(SchedulePort.ScheduleSnapshot.class));
        verify(enrollmentRepository, atLeastOnce()).save(any(Enrollment.class));
    }

    @Test
    void duplicateRegistrationThrowsConflictAndDoesNotPersist() {
        Enrollment existing = enrollment(
                "enrollment-1",
                "member-1",
                List.of(item("session-1", EnrollmentStatus.ENROLLED, "trainer-1", "schedule-1"))
        );

        when(memberPort.findById("member-1")).thenReturn(Optional.of(activeMember("member-1")));
        when(schedulePort.findByClassSessionId("session-1")).thenReturn(Optional.of(schedule("schedule-1", "session-1", "trainer-1", "Yoga", 10, 0)));
        when(trainerPort.findById("trainer-1")).thenReturn(Optional.of(activeTrainer("trainer-1", "Yoga")));
        when(enrollmentRepository.findAllForMember("member-1")).thenReturn(List.of(existing));

        assertThrows(AlreadyRegisteredException.class, () ->
                orchestrator.enrollMemberInClass("member-1", "session-1", "trainer-1", "schedule-1"));

        verify(schedulePort, never()).save(any(SchedulePort.ScheduleSnapshot.class));
        verify(enrollmentRepository, never()).save(any(Enrollment.class));
    }

    @Test
    void inactiveMembershipBlocksOrchestration() {
        when(memberPort.findById("member-1")).thenReturn(Optional.of(
                new MemberPort.MemberSnapshot("member-1", "Member One", "INACTIVE", LocalDate.now().plusDays(1))
        ));

        assertThrows(InvalidMembershipException.class, () ->
                orchestrator.enrollMemberInClass("member-1", "session-1", "trainer-1", "schedule-1"));
        verify(enrollmentRepository, never()).save(any(Enrollment.class));
    }

    @Test
    void missingSchedulePropagatesNotFound() {
        when(memberPort.findById("member-1")).thenReturn(Optional.of(activeMember("member-1")));
        when(schedulePort.findByClassSessionId("session-1")).thenReturn(Optional.empty());

        assertThrows(ClassSessionNotFoundException.class, () ->
                orchestrator.enrollMemberInClass("member-1", "session-1", "trainer-1", "schedule-1"));
        verify(enrollmentRepository, never()).save(any(Enrollment.class));
    }

    @Test
    void inactiveTrainerBlocksOrchestration() {
        when(memberPort.findById("member-1")).thenReturn(Optional.of(activeMember("member-1")));
        when(schedulePort.findByClassSessionId("session-1")).thenReturn(Optional.of(schedule("schedule-1", "session-1", "trainer-1", "Yoga", 10, 0)));
        when(trainerPort.findById("trainer-1")).thenReturn(Optional.of(
                new TrainerPort.TrainerSnapshot("trainer-1", "Trainer One", "Yoga", false)
        ));

        assertThrows(InactiveTrainerException.class, () ->
                orchestrator.enrollMemberInClass("member-1", "session-1", "trainer-1", "schedule-1"));
        verify(enrollmentRepository, never()).save(any(Enrollment.class));
    }

    @Test
    void scheduleMismatchBlocksOrchestration() {
        when(memberPort.findById("member-1")).thenReturn(Optional.of(activeMember("member-1")));
        when(schedulePort.findByClassSessionId("session-1")).thenReturn(Optional.of(schedule("schedule-a", "session-1", "trainer-1", "Yoga", 10, 0)));

        assertThrows(IllegalArgumentException.class, () ->
                orchestrator.enrollMemberInClass("member-1", "session-1", "trainer-1", "schedule-b"));
        verify(enrollmentRepository, never()).save(any(Enrollment.class));
    }

    @Test
    void cancelEnrollmentReleasesSeatAndPersists() {
        Enrollment existing = enrollment(
                "enrollment-1",
                "member-1",
                List.of(item("session-1", EnrollmentStatus.ENROLLED, "trainer-1", "schedule-1"))
        );

        when(memberPort.findById("member-1")).thenReturn(Optional.of(activeMember("member-1")));
        when(enrollmentRepository.findAllForMember("member-1")).thenReturn(List.of(existing));
        when(schedulePort.findById("schedule-1")).thenReturn(Optional.of(schedule("schedule-1", "session-1", "trainer-1", "Yoga", 10, 3)));
        when(schedulePort.save(any(SchedulePort.ScheduleSnapshot.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Enrollment updated = orchestrator.cancelEnrollment("member-1", "session-1");

        assertEquals(EnrollmentStatus.CANCELLED, updated.getRegisteredClasses().get(0).getEnrollmentStatus());
        verify(schedulePort).save(any(SchedulePort.ScheduleSnapshot.class));
        verify(enrollmentRepository).save(any(Enrollment.class));
    }

    private MemberPort.MemberSnapshot activeMember(String memberId) {
        return new MemberPort.MemberSnapshot(memberId, "Member One", "ACTIVE", LocalDate.now().plusDays(7));
    }

    private TrainerPort.TrainerSnapshot activeTrainer(String trainerId, String specialty) {
        return new TrainerPort.TrainerSnapshot(trainerId, "Trainer One", specialty, true);
    }

    private SchedulePort.ScheduleSnapshot schedule(
            String scheduleId,
            String classSessionId,
            String trainerId,
            String classType,
            int maxCapacity,
            int enrolledCount
    ) {
        return new SchedulePort.ScheduleSnapshot(
                scheduleId,
                "Class",
                classType,
                LocalDateTime.of(2030, 1, 1, 9, 0),
                LocalDateTime.of(2030, 1, 1, 10, 0),
                trainerId,
                maxCapacity,
                enrolledCount,
                "room-1",
                "Studio A",
                20,
                classSessionId,
                LocalDate.of(2030, 1, 1),
                "SCHEDULED"
        );
    }

    private Enrollment enrollment(String enrollmentId, String memberId, List<EnrollmentItem> items) {
        return new Enrollment(EnrollmentId.of(enrollmentId), memberId, items);
    }

    private EnrollmentItem item(String classSessionId, EnrollmentStatus status, String trainerId, String scheduleId) {
        return new EnrollmentItem(
                UUID.randomUUID(),
                EnrollmentDate.of(LocalDateTime.of(2026, 1, 1, 10, 0)),
                status,
                ClassSessionId.of(classSessionId),
                trainerId,
                scheduleId,
                1
        );
    }
}
