package com.andre_nathan.gym_webservice.enrollment.application.exception;

import com.andre_nathan.gym_webservice.enrollment.domain.model.EnrollmentId;

public class EnrollmentNotFoundException extends RuntimeException {
    public EnrollmentNotFoundException(EnrollmentId enrollmentId) {
        super("Enrollment not found: " + enrollmentId);
    }
}
