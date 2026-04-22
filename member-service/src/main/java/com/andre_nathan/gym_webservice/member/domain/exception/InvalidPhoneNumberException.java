package com.andre_nathan.gym_webservice.member.domain.exception;

public class InvalidPhoneNumberException extends RuntimeException {
    public InvalidPhoneNumberException() {
        super("Invalid phone number");
    }
}
