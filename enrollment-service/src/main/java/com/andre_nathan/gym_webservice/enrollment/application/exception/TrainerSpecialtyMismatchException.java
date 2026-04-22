package com.andre_nathan.gym_webservice.enrollment.application.exception;

public class TrainerSpecialtyMismatchException extends RuntimeException {
    public TrainerSpecialtyMismatchException(String trainerSpecialty, String classType) {
        super("Trainer specialty does not match class type. specialty=" + trainerSpecialty + ", classType=" + classType);
    }
}
