package com.andre_nathan.gym_webservice.member.domain.model;

import com.andre_nathan.gym_webservice.member.domain.exception.InvalidEmailAddressException;

import java.util.Objects;
import java.util.regex.Pattern;

public final class EmailAddress {
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final String value;

    private EmailAddress(String value) {
        this.value = value;
    }

    public static EmailAddress of(String value) {
        String normalizedValue = Objects.requireNonNull(value, "emailAddress cannot be null").trim();

        if (normalizedValue.isEmpty() || !EMAIL_PATTERN.matcher(normalizedValue).matches()) {
            throw new InvalidEmailAddressException();
        }

        return new EmailAddress(normalizedValue);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof EmailAddress other) && Objects.equals(value, other.value);
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
