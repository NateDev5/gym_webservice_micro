package com.andre_nathan.gym_webservice.member.domain.model;

import com.andre_nathan.gym_webservice.member.domain.exception.InvalidPhoneNumberException;

import java.util.Objects;
import java.util.regex.Pattern;

public final class PhoneNumber {
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[1-9]\\d{7,14}$");

    private final String value;

    private PhoneNumber(String value) {
        this.value = value;
    }

    public static PhoneNumber of(String value) {
        String normalizedValue = Objects.requireNonNull(value, "phoneNumber cannot be null")
                .replaceAll("\\s+", "");

        if (!PHONE_PATTERN.matcher(normalizedValue).matches()) {
            throw new InvalidPhoneNumberException();
        }

        return new PhoneNumber(normalizedValue);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof PhoneNumber other) && Objects.equals(value, other.value);
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
