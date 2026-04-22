package com.andre_nathan.gym_webservice.enrollment.domain.exception;

public class AlreadyRegisteredException extends RuntimeException {
    public AlreadyRegisteredException() {
        super("Member is already enrolled in this class session");
    }
}
