package com.andre_nathan.gym_webservice.enrollment.application.port.out;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public interface SchedulePort {
    Optional<ScheduleSnapshot> findById(String scheduleId);
    Optional<ScheduleSnapshot> findByClassSessionId(String classSessionId);
    ScheduleSnapshot save(ScheduleSnapshot schedule);

    record ScheduleSnapshot(
            String scheduleId,
            String className,
            String classType,
            LocalDateTime startTime,
            LocalDateTime endTime,
            String trainerId,
            int maxCapacity,
            int enrolledCount,
            String roomId,
            String roomName,
            int roomCapacity,
            String classSessionId,
            LocalDate sessionDate,
            String sessionStatus
    ) {
        public boolean hasAvailableSeat() {
            return enrolledCount < maxCapacity;
        }

        public int nextSeatNumber() {
            return enrolledCount + 1;
        }

        public ScheduleSnapshot incrementEnrollment() {
            return new ScheduleSnapshot(
                    scheduleId,
                    className,
                    classType,
                    startTime,
                    endTime,
                    trainerId,
                    maxCapacity,
                    enrolledCount + 1,
                    roomId,
                    roomName,
                    roomCapacity,
                    classSessionId,
                    sessionDate,
                    sessionStatus
            );
        }

        public ScheduleSnapshot decrementEnrollment() {
            int updatedEnrolledCount = enrolledCount > 0 ? enrolledCount - 1 : 0;
            return new ScheduleSnapshot(
                    scheduleId,
                    className,
                    classType,
                    startTime,
                    endTime,
                    trainerId,
                    maxCapacity,
                    updatedEnrolledCount,
                    roomId,
                    roomName,
                    roomCapacity,
                    classSessionId,
                    sessionDate,
                    sessionStatus
            );
        }
    }
}
