package com.andre_nathan.gym_webservice.schedule.domain.model;

import com.andre_nathan.gym_webservice.schedule.domain.exception.InvalidCapacityException;
import com.andre_nathan.gym_webservice.schedule.domain.exception.InvalidTimeSlotException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ScheduleDomainTest {

    @Test
    void createsValidScheduleAndSupportsSeatChanges() {
        Schedule schedule = schedule(
                10,
                2,
                LocalDateTime.of(2026, 6, 1, 9, 0),
                LocalDateTime.of(2026, 6, 1, 10, 0)
        );

        assertTrue(schedule.hasAvailableSeat());
        assertEquals(3, schedule.nextSeatNumber());

        schedule.incrementEnrollment();
        assertEquals(3, schedule.getEnrolledCount());

        schedule.decrementEnrollment();
        assertEquals(2, schedule.getEnrolledCount());
    }

    @Test
    void rejectsInvalidCapacityRules() {
        assertThrows(InvalidCapacityException.class, () -> schedule(
                0,
                0,
                LocalDateTime.of(2026, 6, 1, 9, 0),
                LocalDateTime.of(2026, 6, 1, 10, 0)
        ));

        assertThrows(InvalidCapacityException.class, () -> schedule(
                5,
                6,
                LocalDateTime.of(2026, 6, 1, 9, 0),
                LocalDateTime.of(2026, 6, 1, 10, 0)
        ));
    }

    @Test
    void rejectsInvalidTimeSlot() {
        assertThrows(InvalidTimeSlotException.class, () -> new TimeSlot(
                LocalDateTime.of(2026, 6, 1, 10, 0),
                LocalDateTime.of(2026, 6, 1, 10, 0)
        ));
    }

    @Test
    void detectsOverlappingSchedules() {
        Schedule first = schedule(
                10,
                1,
                LocalDateTime.of(2026, 6, 1, 9, 0),
                LocalDateTime.of(2026, 6, 1, 10, 0)
        );
        Schedule second = schedule(
                10,
                1,
                LocalDateTime.of(2026, 6, 1, 9, 30),
                LocalDateTime.of(2026, 6, 1, 10, 30)
        );

        assertTrue(first.overlapsWith(second));
    }

    private Schedule schedule(int maxCapacity, int enrolledCount, LocalDateTime start, LocalDateTime end) {
        return new Schedule(
                ScheduleId.newId(),
                "Morning Yoga",
                "Yoga",
                new TimeSlot(start, end),
                new Room(RoomId.of("room-1"), "Studio A", 20),
                "trainer-1",
                maxCapacity,
                enrolledCount,
                new ClassSession(ClassSessionId.of("session-1"), LocalDate.of(2026, 6, 1), "SCHEDULED")
        );
    }
}
