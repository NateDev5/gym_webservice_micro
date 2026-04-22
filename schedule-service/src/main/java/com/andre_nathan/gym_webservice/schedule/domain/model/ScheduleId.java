package com.andre_nathan.gym_webservice.schedule.domain.model;

import java.util.Objects;
import java.util.UUID;

public final class ScheduleId {
    private final String value;

    private ScheduleId(String value) {
        this.value = Objects.requireNonNull(value, "scheduleId cannot be null");
    }

    public static ScheduleId newId() {
        return new ScheduleId(UUID.randomUUID().toString());
    }

    public static ScheduleId of(String value) {
        return new ScheduleId(value);
    }

    public String value() {
        return value;
    }
}
