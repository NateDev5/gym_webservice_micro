package com.andre_nathan.gym_webservice.schedule.domain.model;

import java.util.Objects;
import java.util.UUID;

public final class ClassSessionId {
    private final String value;

    private ClassSessionId(String value) {
        this.value = Objects.requireNonNull(value, "classSessionId cannot be null");
    }

    public static ClassSessionId newId() {
        return new ClassSessionId(UUID.randomUUID().toString());
    }

    public static ClassSessionId of(String value) {
        return new ClassSessionId(value);
    }

    public String value() {
        return value;
    }
}
