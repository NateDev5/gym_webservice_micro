package com.andre_nathan.gym_webservice.schedule.application.exception;

import com.andre_nathan.gym_webservice.schedule.domain.model.ScheduleId;

public class ScheduleNotFoundException extends RuntimeException {
    public ScheduleNotFoundException(ScheduleId scheduleId) {
        super("Schedule " + scheduleId.value() + " was not found.");
    }
}
