package com.andre_nathan.gym_webservice.schedule.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("testing")
class ScheduleRepositoryIntegrationTest {

    @Autowired
    private SpringDataScheduleRepository scheduleRepository;

    @BeforeEach
    void setUp() {
        scheduleRepository.deleteAll();
    }

    @Test
    void savesAndFindsById() {
        ScheduleJpaEntity saved = scheduleRepository.save(newSchedule("schedule-1", "session-1"));

        assertTrue(scheduleRepository.findById(saved.scheduleId).isPresent());
    }

    @Test
    void findsByMeaningfulFieldAndMissingReturnsEmpty() {
        scheduleRepository.save(newSchedule("schedule-2", "session-2"));

        assertTrue(scheduleRepository.findByClassSessionId("session-2").isPresent());
        assertTrue(scheduleRepository.findByClassSessionId("missing-session").isEmpty());
    }

    @Test
    void deleteAndMissingReturnsEmpty() {
        scheduleRepository.save(newSchedule("schedule-3", "session-3"));
        assertTrue(scheduleRepository.existsById("schedule-3"));

        scheduleRepository.deleteById("schedule-3");

        assertTrue(scheduleRepository.findById("schedule-3").isEmpty());
        assertFalse(scheduleRepository.existsById("schedule-3"));
    }

    private ScheduleJpaEntity newSchedule(String scheduleId, String classSessionId) {
        ScheduleJpaEntity entity = new ScheduleJpaEntity();
        entity.scheduleId = scheduleId;
        entity.className = "Morning Yoga";
        entity.classType = "Yoga";
        entity.startTime = LocalDateTime.of(2026, 7, 1, 9, 0);
        entity.endTime = LocalDateTime.of(2026, 7, 1, 10, 0);
        entity.roomId = "room-1";
        entity.roomName = "Studio A";
        entity.roomCapacity = 20;
        entity.trainerId = "trainer-1";
        entity.maxCapacity = 20;
        entity.enrolledCount = 1;
        entity.classSessionId = classSessionId;
        entity.sessionDate = LocalDate.of(2026, 7, 1);
        entity.sessionStatus = "SCHEDULED";
        return entity;
    }
}
