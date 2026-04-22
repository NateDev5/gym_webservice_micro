package com.andre_nathan.gym_webservice.schedule.application.exception;

public class TrainerScheduleConflictException extends RuntimeException {
    public TrainerScheduleConflictException(String trainerId) {
        super("Trainer " + trainerId + " already has an overlapping class.");
    }
}
