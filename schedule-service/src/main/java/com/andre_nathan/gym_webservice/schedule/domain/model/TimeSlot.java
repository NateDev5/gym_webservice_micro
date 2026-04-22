package com.andre_nathan.gym_webservice.schedule.domain.model;

import com.andre_nathan.gym_webservice.schedule.domain.exception.InvalidTimeSlotException;

import java.time.LocalDateTime;
import java.util.Objects;

public record TimeSlot(LocalDateTime start, LocalDateTime end) {
    public TimeSlot {
        Objects.requireNonNull(start, "start cannot be null");
        Objects.requireNonNull(end, "end cannot be null");

        if (!end.isAfter(start)) {
            throw new InvalidTimeSlotException("endTime must be after startTime");
        }
    }

    public boolean overlaps(TimeSlot other) {
        return start.isBefore(other.end()) && end.isAfter(other.start());
    }
}
