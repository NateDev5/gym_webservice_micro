package com.andre_nathan.gym_webservice.trainer.domain.model;

import com.andre_nathan.gym_webservice.trainer.domain.exception.InvalidTrainerSpecialtyException;

import java.util.Locale;
import java.util.Objects;

public final class Specialty {
    private final String value;

    private Specialty(String value) {
        this.value = value;
    }

    public static Specialty of(String value) {
        String normalizedValue = Objects.requireNonNull(value, "specialty cannot be null")
                .trim()
                .replaceAll("\\s+", " ");

        if (normalizedValue.length() < 3) {
            throw new InvalidTrainerSpecialtyException();
        }

        return new Specialty(normalizedValue);
    }

    public String value() {
        return value;
    }

    public boolean matches(String classType) {
        String normalizedClassType = Objects.requireNonNull(classType, "classType cannot be null")
                .trim()
                .toLowerCase(Locale.ROOT);
        String normalizedValue = value.toLowerCase(Locale.ROOT);

        return normalizedValue.equals(normalizedClassType)
                || normalizedValue.contains(normalizedClassType)
                || normalizedClassType.contains(normalizedValue);
    }
}
