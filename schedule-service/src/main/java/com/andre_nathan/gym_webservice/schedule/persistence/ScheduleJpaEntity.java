package com.andre_nathan.gym_webservice.schedule.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "schedules")
public class ScheduleJpaEntity {
    @Id
    @Column(name = "schedule_id", nullable = false, updatable = false)
    public String scheduleId;

    @Column(name = "class_name", nullable = false)
    public String className;

    @Column(name = "class_type", nullable = false)
    public String classType;

    @Column(name = "start_time", nullable = false)
    public LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    public LocalDateTime endTime;

    @Column(name = "room_id", nullable = false)
    public String roomId;

    @Column(name = "room_name", nullable = false)
    public String roomName;

    @Column(name = "room_capacity", nullable = false)
    public int roomCapacity;

    @Column(name = "trainer_id", nullable = false)
    public String trainerId;

    @Column(name = "max_capacity", nullable = false)
    public int maxCapacity;

    @Column(name = "enrolled_count", nullable = false)
    public int enrolledCount;

    @Column(name = "class_session_id", nullable = false, unique = true)
    public String classSessionId;

    @Column(name = "session_date", nullable = false)
    public LocalDate sessionDate;

    @Column(name = "session_status", nullable = false)
    public String sessionStatus;

    protected ScheduleJpaEntity() {
    }
}
