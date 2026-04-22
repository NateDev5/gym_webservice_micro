package com.andre_nathan.gym_webservice.enrollment.application.exception;

public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException(String memberId) {
        super("Member not found: " + memberId);
    }
}
