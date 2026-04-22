package com.andre_nathan.gym_webservice.member.domain.model;

import com.andre_nathan.gym_webservice.member.domain.exception.InvalidFullNameException;

import java.util.Objects;

public final class FullName {
    private final String value;

    private FullName(String value) {
        this.value = value;
    }

    public static FullName of(String value) {
        String normalizedValue = Objects.requireNonNull(value, "fullName cannot be null")
                .trim()
                .replaceAll("\\s+", " ");

        if (normalizedValue.length() < 3) {
            throw new InvalidFullNameException();
        }

        return new FullName(normalizedValue);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof FullName other) && Objects.equals(value, other.value);
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
