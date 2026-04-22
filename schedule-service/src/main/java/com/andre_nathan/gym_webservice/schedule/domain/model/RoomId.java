package com.andre_nathan.gym_webservice.schedule.domain.model;

import java.util.Objects;
import java.util.UUID;

public final class RoomId {
    private final String value;

    private RoomId(String value) {
        this.value = Objects.requireNonNull(value, "roomId cannot be null");
    }

    public static RoomId newId() {
        return new RoomId(UUID.randomUUID().toString());
    }

    public static RoomId of(String value) {
        return new RoomId(value);
    }

    public String value() {
        return value;
    }
}
