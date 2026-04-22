package com.andre_nathan.gym_webservice.trainer.application.exception;

public class TrainerSpecialtyMismatchException extends RuntimeException {
    public TrainerSpecialtyMismatchException(String specialty, String classType) {
        super("Trainer specialty " + specialty + " does not match class type " + classType + ".");
    }
}
