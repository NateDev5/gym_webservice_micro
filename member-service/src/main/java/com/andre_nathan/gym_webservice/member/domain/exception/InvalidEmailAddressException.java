package com.andre_nathan.gym_webservice.member.domain.exception;

public class InvalidEmailAddressException extends RuntimeException {
    public InvalidEmailAddressException() {
        super("Invalid email address");
    }
}
