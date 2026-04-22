package com.andre_nathan.gym_webservice.enrollment.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

public final class EnrollmentDate {
    private final LocalDateTime value;

    private EnrollmentDate(LocalDateTime value) {
        this.value = value;
    }

    public static EnrollmentDate now() {
        return new EnrollmentDate(LocalDateTime.now());
    }

    public static EnrollmentDate of(LocalDateTime value) {
        return new EnrollmentDate(Objects.requireNonNull(value, "enrollmentDate cannot be null"));
    }

    public LocalDateTime value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof EnrollmentDate other) && Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
