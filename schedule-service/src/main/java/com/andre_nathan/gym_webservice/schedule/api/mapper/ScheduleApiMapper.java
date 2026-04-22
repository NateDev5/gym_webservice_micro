package com.andre_nathan.gym_webservice.schedule.api.mapper;

import com.andre_nathan.gym_webservice.schedule.api.dto.ScheduleResponse;
import com.andre_nathan.gym_webservice.schedule.domain.model.Schedule;

public final class ScheduleApiMapper {
    private ScheduleApiMapper() {
    }

    public static ScheduleResponse toResponse(Schedule schedule) {
        return new ScheduleResponse(
                schedule.getScheduleId().value(),
                schedule.getClassName(),
                schedule.getClassType(),
                schedule.getTimeSlot().start(),
                schedule.getTimeSlot().end(),
                schedule.getTrainerId(),
                schedule.getMaxCapacity(),
                schedule.getEnrolledCount(),
                new ScheduleResponse.RoomResponse(
                        schedule.getRoom().getRoomId().value(),
                        schedule.getRoom().getRoomName(),
                        schedule.getRoom().getRoomCapacity()
                ),
                new ScheduleResponse.ClassSessionResponse(
                        schedule.getClassSession().getClassSessionId().value(),
                        schedule.getClassSession().getSessionDate(),
                        schedule.getClassSession().getSessionStatus()
                )
        );
    }
}
