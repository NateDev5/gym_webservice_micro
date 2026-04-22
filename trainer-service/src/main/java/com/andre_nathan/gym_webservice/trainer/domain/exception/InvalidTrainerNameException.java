package com.andre_nathan.gym_webservice.trainer.domain.exception;

public class InvalidTrainerNameException extends RuntimeException {
    public InvalidTrainerNameException() {
        super("Trainer full name must contain at least 3 characters.");
    }
}
