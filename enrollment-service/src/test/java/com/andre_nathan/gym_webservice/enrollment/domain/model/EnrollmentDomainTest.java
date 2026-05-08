package com.andre_nathan.gym_webservice.enrollment.domain.model;

import com.andre_nathan.gym_webservice.enrollment.domain.exception.AlreadyRegisteredException;
import com.andre_nathan.gym_webservice.enrollment.domain.exception.EnrollmentRecordNotFoundException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EnrollmentDomainTest {

    @Test
    void enrollAddsNewRegistration() {
        Enrollment enrollment = new Enrollment(
                EnrollmentId.newId(),
                "member-1",
                List.of()
        );

        enrollment.enroll(ClassSessionId.of("session-1"), "trainer-1", "schedule-1", 1);

        assertEquals(1, enrollment.getRegisteredClasses().size());
        assertEquals(EnrollmentStatus.ENROLLED, enrollment.getRegisteredClasses().get(0).getEnrollmentStatus());
    }

    @Test
    void enrollingSameActiveClassTwiceThrowsDuplicate() {
        Enrollment enrollment = new Enrollment(
                EnrollmentId.newId(),
                "member-1",
                List.of(item("session-1", EnrollmentStatus.ENROLLED))
        );

        assertThrows(AlreadyRegisteredException.class, () ->
                enrollment.enroll(ClassSessionId.of("session-1"), "trainer-1", "schedule-1", 2));
    }

    @Test
    void cancelEnrollmentMarksItemAsCancelled() {
        Enrollment enrollment = new Enrollment(
                EnrollmentId.newId(),
                "member-1",
                List.of(item("session-1", EnrollmentStatus.ENROLLED))
        );

        enrollment.cancelEnrollment(ClassSessionId.of("session-1"));

        assertEquals(EnrollmentStatus.CANCELLED, enrollment.getRegisteredClasses().get(0).getEnrollmentStatus());
    }

    @Test
    void getRegistrationForMissingSessionThrows() {
        Enrollment enrollment = new Enrollment(
                EnrollmentId.newId(),
                "member-1",
                List.of()
        );

        assertThrows(EnrollmentRecordNotFoundException.class, () ->
                enrollment.getRegistrationFor(ClassSessionId.of("missing")));
    }

    private EnrollmentItem item(String classSessionId, EnrollmentStatus status) {
        return new EnrollmentItem(
                UUID.randomUUID(),
                EnrollmentDate.of(LocalDateTime.of(2026, 1, 1, 10, 0)),
                status,
                ClassSessionId.of(classSessionId),
                "trainer-1",
                "schedule-1",
                1
        );
    }
}
