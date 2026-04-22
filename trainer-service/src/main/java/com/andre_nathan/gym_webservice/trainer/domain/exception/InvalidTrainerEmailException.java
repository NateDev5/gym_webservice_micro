package com.andre_nathan.gym_webservice.trainer.domain.exception;

public class InvalidTrainerEmailException extends RuntimeException {
    public InvalidTrainerEmailException() {
        super("Trainer email address is invalid.");
    }
}
