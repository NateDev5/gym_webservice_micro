package com.andre_nathan.gym_webservice.enrollment.application.exception;

public class InvalidMembershipException extends RuntimeException {
    public InvalidMembershipException(String memberId) {
        super("Member does not have a valid membership: " + memberId);
    }
}
