package com.andre_nathan.gym_webservice.enrollment.infrastructure.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("testing")
class EnrollmentRepositoryIntegrationTest {

    @Autowired
    private SpringDataEnrollmentRepository enrollmentRepository;

    @BeforeEach
    void setUp() {
        enrollmentRepository.deleteAll();
    }

    @Test
    void savesAndFindsById() {
        EnrollmentJpaEntity saved = enrollmentRepository.save(newEnrollment("enrollment-1", "member-1"));

        assertTrue(enrollmentRepository.findById(saved.enrollmentId).isPresent());
    }

    @Test
    void findsByMeaningfulFieldAndMissingReturnsEmpty() {
        enrollmentRepository.save(newEnrollment("enrollment-2", "member-2"));

        assertEquals(1, enrollmentRepository.findAllByMemberId("member-2").size());
        assertTrue(enrollmentRepository.findAllByMemberId("missing-member").isEmpty());
    }

    @Test
    void deleteAndMissingReturnEmptyOrFalse() {
        enrollmentRepository.save(newEnrollment("enrollment-3", "member-3"));

        enrollmentRepository.deleteById("enrollment-3");

        assertTrue(enrollmentRepository.findById("enrollment-3").isEmpty());
        assertFalse(enrollmentRepository.existsById("enrollment-3"));
    }

    private EnrollmentJpaEntity newEnrollment(String enrollmentId, String memberId) {
        EnrollmentJpaEntity enrollment = new EnrollmentJpaEntity();
        enrollment.enrollmentId = enrollmentId;
        enrollment.memberId = memberId;

        EnrollmentItemJpaEntity item = new EnrollmentItemJpaEntity();
        item.registrationId = UUID.randomUUID();
        item.enrollmentDate = LocalDateTime.of(2026, 1, 1, 10, 0);
        item.enrollmentStatus = "ENROLLED";
        item.classSessionId = "session-1";
        item.trainerId = "trainer-1";
        item.scheduleId = "schedule-1";
        item.seatNumber = 1;
        item.enrollment = enrollment;
        enrollment.registeredClasses.add(item);

        return enrollment;
    }
}
