package com.andre_nathan.gym_webservice.member.domain.model;

import java.util.Objects;
import java.util.UUID;

public final class MemberId {
    private final String value;

    private MemberId (String value) {
        this.value = value;
    }

    public static MemberId newId () {
        return new MemberId(UUID.randomUUID().toString());
    }

    public static MemberId of (String value) {
        return new MemberId(value);
    }

    public String value() { return value; }

    @Override public boolean equals (Object o) {
        return (o instanceof MemberId other) && Objects.equals(value, other.value);
    }
    @Override public int hashCode() { return Objects.hash(value); }
    @Override public String toString() { return value; }
}

