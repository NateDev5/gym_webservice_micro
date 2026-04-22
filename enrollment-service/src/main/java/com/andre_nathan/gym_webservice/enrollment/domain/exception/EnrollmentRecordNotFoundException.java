package com.andre_nathan.gym_webservice.enrollment.domain.exception;

public class EnrollmentRecordNotFoundException extends RuntimeException {
    public EnrollmentRecordNotFoundException(String classSessionId) {
        super("Member is not enrolled in class session " + classSessionId);
    }
}
