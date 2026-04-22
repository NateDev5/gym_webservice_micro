package com.andre_nathan.gym_webservice.trainer.domain.model;

import com.andre_nathan.gym_webservice.trainer.domain.exception.InvalidTrainerNameException;

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
            throw new InvalidTrainerNameException();
        }

        return new FullName(normalizedValue);
    }

    public String value() {
        return value;
    }
}
