package com.andre_nathan.gym_webservice.trainer.domain.model;

import java.util.Objects;
import java.util.UUID;

public final class TrainerId {
    private final String value;

    private TrainerId(String value) {
        this.value = Objects.requireNonNull(value, "trainerId cannot be null");
    }

    public static TrainerId newId() {
        return new TrainerId(UUID.randomUUID().toString());
    }

    public static TrainerId of(String value) {
        return new TrainerId(value);
    }

    public String value() {
        return value;
    }
}
