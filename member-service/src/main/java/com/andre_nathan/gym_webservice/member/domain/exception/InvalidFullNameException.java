package com.andre_nathan.gym_webservice.member.domain.exception;

public class InvalidFullNameException extends RuntimeException {
    public InvalidFullNameException() {
        super("Invalid full name");
    }
}
