package com.andre_nathan.gym_webservice.trainer.application.exception;

public class DuplicateTrainerException extends RuntimeException {
    public DuplicateTrainerException(String email) {
        super("Trainer with email " + email + " already exists.");
    }
}
