package com.andre_nathan.gym_webservice.schedule.application.exception;

public class RoomBookingConflictException extends RuntimeException {
    public RoomBookingConflictException(String roomId) {
        super("Room " + roomId + " already has a class scheduled during this time.");
    }
}
