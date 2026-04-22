package com.andre_nathan.gym_webservice.enrollment.application.exception;

public class TrainerNotFoundException extends RuntimeException {
    public TrainerNotFoundException(String trainerId) {
        super("Trainer not found: " + trainerId);
    }
}
