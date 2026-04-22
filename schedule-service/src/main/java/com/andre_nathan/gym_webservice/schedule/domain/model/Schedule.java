package com.andre_nathan.gym_webservice.schedule.domain.model;

import com.andre_nathan.gym_webservice.schedule.domain.exception.InvalidCapacityException;

import java.util.Objects;

public class Schedule {
    private final ScheduleId scheduleId;
    private final String className;
    private final String classType;
    private final TimeSlot timeSlot;
    private final Room room;
    private final String trainerId;
    private final int maxCapacity;
    private int enrolledCount;
    private final ClassSession classSession;

    public Schedule(
            ScheduleId scheduleId,
            String className,
            String classType,
            TimeSlot timeSlot,
            Room room,
            String trainerId,
            int maxCapacity,
            int enrolledCount,
            ClassSession classSession
    ) {
        this.scheduleId = Objects.requireNonNull(scheduleId, "scheduleId cannot be null");
        this.className = Objects.requireNonNull(className, "className cannot be null").trim();
        this.classType = Objects.requireNonNull(classType, "classType cannot be null").trim();
        this.timeSlot = Objects.requireNonNull(timeSlot, "timeSlot cannot be null");
        this.room = Objects.requireNonNull(room, "room cannot be null");
        this.trainerId = Objects.requireNonNull(trainerId, "trainerId cannot be null").trim();
        this.maxCapacity = maxCapacity;
        this.enrolledCount = enrolledCount;
        this.classSession = Objects.requireNonNull(classSession, "classSession cannot be null");

        if (maxCapacity <= 0) {
            throw new InvalidCapacityException("maxCapacity must be greater than 0");
        }

        if (maxCapacity > room.getRoomCapacity()) {
            throw new InvalidCapacityException("maxCapacity cannot exceed roomCapacity");
        }

        if (enrolledCount < 0 || enrolledCount > maxCapacity) {
            throw new InvalidCapacityException("enrolledCount must be between 0 and maxCapacity");
        }
    }

    public ScheduleId getScheduleId() {
        return scheduleId;
    }

    public String getClassName() {
        return className;
    }

    public String getClassType() {
        return classType;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public Room getRoom() {
        return room;
    }

    public String getTrainerId() {
        return trainerId;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public int getEnrolledCount() {
        return enrolledCount;
    }

    public ClassSession getClassSession() {
        return classSession;
    }

    public boolean hasAvailableSeat() {
        return enrolledCount < maxCapacity;
    }

    public int nextSeatNumber() {
        return enrolledCount + 1;
    }

    public void incrementEnrollment() {
        if (!hasAvailableSeat()) {
            throw new InvalidCapacityException("enrolledCount cannot be more than maxCapacity");
        }
        enrolledCount++;
    }

    public void decrementEnrollment() {
        if (enrolledCount > 0) {
            enrolledCount--;
        }
    }

    public boolean overlapsWith(Schedule other) {
        return this.timeSlot.overlaps(other.timeSlot);
    }
}
