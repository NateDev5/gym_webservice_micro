package com.andre_nathan.gym_webservice.schedule.domain.model;

import java.time.LocalDate;
import java.util.Objects;

public class ClassSession {
    private final ClassSessionId classSessionId;
    private final LocalDate sessionDate;
    private final String sessionStatus;

    public ClassSession(ClassSessionId classSessionId, LocalDate sessionDate, String sessionStatus) {
        this.classSessionId = Objects.requireNonNull(classSessionId, "classSessionId cannot be null");
        this.sessionDate = Objects.requireNonNull(sessionDate, "sessionDate cannot be null");
        this.sessionStatus = Objects.requireNonNull(sessionStatus, "sessionStatus cannot be null").trim();
    }

    public ClassSessionId getClassSessionId() {
        return classSessionId;
    }

    public LocalDate getSessionDate() {
        return sessionDate;
    }

    public String getSessionStatus() {
        return sessionStatus;
    }
}
