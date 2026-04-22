package com.andre_nathan.gym_webservice.schedule.persistence;

import com.andre_nathan.gym_webservice.schedule.application.port.out.ScheduleRepositoryPort;
import com.andre_nathan.gym_webservice.schedule.domain.model.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class JpaScheduleRepositoryAdapter implements ScheduleRepositoryPort {
    private final SpringDataScheduleRepository repository;

    public JpaScheduleRepositoryAdapter(SpringDataScheduleRepository repository) {
        this.repository = repository;
    }

    @Override
    public Schedule save(Schedule schedule) {
        repository.save(toEntity(schedule));
        return schedule;
    }

    @Override
    public Optional<Schedule> findById(ScheduleId scheduleId) {
        return repository.findById(scheduleId.value()).map(this::toDomain);
    }

    @Override
    public Optional<Schedule> findByClassSessionId(String classSessionId) {
        return repository.findByClassSessionId(classSessionId).map(this::toDomain);
    }

    @Override
    public List<Schedule> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteById(ScheduleId scheduleId) {
        repository.deleteById(scheduleId.value());
    }

    private ScheduleJpaEntity toEntity(Schedule schedule) {
        ScheduleJpaEntity entity = new ScheduleJpaEntity();
        entity.scheduleId = schedule.getScheduleId().value();
        entity.className = schedule.getClassName();
        entity.classType = schedule.getClassType();
        entity.startTime = schedule.getTimeSlot().start();
        entity.endTime = schedule.getTimeSlot().end();
        entity.roomId = schedule.getRoom().getRoomId().value();
        entity.roomName = schedule.getRoom().getRoomName();
        entity.roomCapacity = schedule.getRoom().getRoomCapacity();
        entity.trainerId = schedule.getTrainerId();
        entity.maxCapacity = schedule.getMaxCapacity();
        entity.enrolledCount = schedule.getEnrolledCount();
        entity.classSessionId = schedule.getClassSession().getClassSessionId().value();
        entity.sessionDate = schedule.getClassSession().getSessionDate();
        entity.sessionStatus = schedule.getClassSession().getSessionStatus();
        return entity;
    }

    private Schedule toDomain(ScheduleJpaEntity entity) {
        return new Schedule(
                ScheduleId.of(entity.scheduleId),
                entity.className,
                entity.classType,
                new TimeSlot(entity.startTime, entity.endTime),
                new Room(RoomId.of(entity.roomId), entity.roomName, entity.roomCapacity),
                entity.trainerId,
                entity.maxCapacity,
                entity.enrolledCount,
                new ClassSession(ClassSessionId.of(entity.classSessionId), entity.sessionDate, entity.sessionStatus)
        );
    }
}
