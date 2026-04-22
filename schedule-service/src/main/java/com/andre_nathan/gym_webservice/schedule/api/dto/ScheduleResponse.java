package com.andre_nathan.gym_webservice.schedule.api.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ScheduleResponse(
        String scheduleId,
        String className,
        String classType,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String trainerId,
        int maxCapacity,
        int enrolledCount,
        RoomResponse room,
        ClassSessionResponse classSession
) {
    public record RoomResponse(
            String roomId,
            String roomName,
            int roomCapacity
    ) {
    }

    public record ClassSessionResponse(
            String classSessionId,
            LocalDate sessionDate,
            String sessionStatus
    ) {
    }
}
