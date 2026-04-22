package com.andre_nathan.gym_webservice.enrollment.domain.model;

import java.util.Objects;
import java.util.UUID;

public final class ClassSessionId {
    private final String value;

    private ClassSessionId(String value) {
        this.value = value;
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

    @Override
    public boolean equals(Object o) {
        return (o instanceof ClassSessionId other) && Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
