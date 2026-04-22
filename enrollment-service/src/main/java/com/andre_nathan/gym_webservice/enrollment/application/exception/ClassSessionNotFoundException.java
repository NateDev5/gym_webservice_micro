package com.andre_nathan.gym_webservice.enrollment.application.exception;

public class ClassSessionNotFoundException extends RuntimeException {
    public ClassSessionNotFoundException(String classSessionId) {
        super("Class session not found: " + classSessionId);
    }
}
