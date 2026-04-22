package com.andre_nathan.gym_webservice.schedule.domain.exception;

public class InvalidTimeSlotException extends RuntimeException {
    public InvalidTimeSlotException(String message) {
        super(message);
    }
}
