package com.andre_nathan.gym_webservice.schedule.application.service;

import com.andre_nathan.gym_webservice.schedule.application.exception.ClassSessionNotFoundException;
import com.andre_nathan.gym_webservice.schedule.application.exception.RoomBookingConflictException;
import com.andre_nathan.gym_webservice.schedule.application.exception.ScheduleNotFoundException;
import com.andre_nathan.gym_webservice.schedule.application.exception.TrainerScheduleConflictException;
import com.andre_nathan.gym_webservice.schedule.application.port.out.ScheduleRepositoryPort;
import com.andre_nathan.gym_webservice.schedule.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduleCrudService {
    private final ScheduleRepositoryPort repository;

    public ScheduleCrudService(ScheduleRepositoryPort repository) {
        this.repository = repository;
    }

    @Transactional
    public Schedule create(
            String className,
            String classType,
            LocalDateTime startTime,
            LocalDateTime endTime,
            String roomId,
            String roomName,
            int roomCapacity,
            String trainerId,
            int maxCapacity,
            int enrolledCount,
            String classSessionId,
            LocalDate sessionDate,
            String sessionStatus
    ) {
        Schedule schedule = buildSchedule(
                ScheduleId.newId(),
                className,
                classType,
                startTime,
                endTime,
                roomId,
                roomName,
                roomCapacity,
                trainerId,
                maxCapacity,
                enrolledCount,
                classSessionId,
                sessionDate,
                sessionStatus
        );
        validateConflicts(schedule, null);
        return repository.save(schedule);
    }

    @Transactional
    public Schedule update(
            String scheduleId,
            String className,
            String classType,
            LocalDateTime startTime,
            LocalDateTime endTime,
            String roomId,
            String roomName,
            int roomCapacity,
            String trainerId,
            int maxCapacity,
            int enrolledCount,
            String classSessionId,
            LocalDate sessionDate,
            String sessionStatus
    ) {
        ScheduleId parsedScheduleId = ScheduleId.of(scheduleId);
        getById(parsedScheduleId);

        Schedule schedule = buildSchedule(
                parsedScheduleId,
                className,
                classType,
                startTime,
                endTime,
                roomId,
                roomName,
                roomCapacity,
                trainerId,
                maxCapacity,
                enrolledCount,
                classSessionId,
                sessionDate,
                sessionStatus
        );
        validateConflicts(schedule, parsedScheduleId);
        return repository.save(schedule);
    }

    @Transactional(readOnly = true)
    public Schedule getById(String scheduleId) {
        return getById(ScheduleId.of(scheduleId));
    }

    @Transactional(readOnly = true)
    public List<Schedule> getAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Schedule getByClassSessionId(String classSessionId) {
        return repository.findByClassSessionId(classSessionId)
                .orElseThrow(() -> new ClassSessionNotFoundException(classSessionId));
    }

    @Transactional
    public Schedule reserveSeat(String classSessionId) {
        Schedule schedule = getByClassSessionId(classSessionId);
        schedule.incrementEnrollment();
        return repository.save(schedule);
    }

    @Transactional
    public Schedule releaseSeat(String classSessionId) {
        Schedule schedule = getByClassSessionId(classSessionId);
        schedule.decrementEnrollment();
        return repository.save(schedule);
    }

    @Transactional
    public void delete(String scheduleId) {
        ScheduleId parsedScheduleId = ScheduleId.of(scheduleId);
        getById(parsedScheduleId);
        repository.deleteById(parsedScheduleId);
    }

    private Schedule getById(ScheduleId scheduleId) {
        return repository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleNotFoundException(scheduleId));
    }

    private Schedule buildSchedule(
            ScheduleId scheduleId,
            String className,
            String classType,
            LocalDateTime startTime,
            LocalDateTime endTime,
            String roomId,
            String roomName,
            int roomCapacity,
            String trainerId,
            int maxCapacity,
            int enrolledCount,
            String classSessionId,
            LocalDate sessionDate,
            String sessionStatus
    ) {
        return new Schedule(
                scheduleId,
                className,
                classType,
                new TimeSlot(startTime, endTime),
                new Room(RoomId.of(roomId), roomName, roomCapacity),
                trainerId,
                maxCapacity,
                enrolledCount,
                new ClassSession(ClassSessionId.of(classSessionId), sessionDate, sessionStatus)
        );
    }

    private void validateConflicts(Schedule candidate, ScheduleId excludedScheduleId) {
        for (Schedule existing : repository.findAll()) {
            if (excludedScheduleId != null && existing.getScheduleId().value().equals(excludedScheduleId.value())) {
                continue;
            }

            if (!existing.overlapsWith(candidate)) {
                continue;
            }

            if (existing.getRoom().getRoomId().value().equals(candidate.getRoom().getRoomId().value())) {
                throw new RoomBookingConflictException(candidate.getRoom().getRoomId().value());
            }

            if (existing.getTrainerId().equals(candidate.getTrainerId())) {
                throw new TrainerScheduleConflictException(candidate.getTrainerId());
            }
        }
    }
}
