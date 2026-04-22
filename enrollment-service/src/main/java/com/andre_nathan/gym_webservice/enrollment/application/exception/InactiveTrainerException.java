package com.andre_nathan.gym_webservice.enrollment.application.exception;

public class InactiveTrainerException extends RuntimeException {
    public InactiveTrainerException(String trainerId) {
        super("Trainer is inactive: " + trainerId);
    }
}
