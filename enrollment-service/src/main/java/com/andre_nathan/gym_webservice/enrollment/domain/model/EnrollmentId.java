package com.andre_nathan.gym_webservice.enrollment.domain.model;

import java.util.Objects;
import java.util.UUID;

public final class EnrollmentId {
    private final String value;

    private EnrollmentId(String value) {
        this.value = value;
    }

    public static EnrollmentId newId() {
        return new EnrollmentId(UUID.randomUUID().toString());
    }

    public static EnrollmentId of(String value) {
        return new EnrollmentId(value);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof EnrollmentId other) && Objects.equals(value, other.value);
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
