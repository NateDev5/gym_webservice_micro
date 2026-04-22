package com.andre_nathan.gym_webservice.schedule.application.port.out;

import com.andre_nathan.gym_webservice.schedule.domain.model.Schedule;
import com.andre_nathan.gym_webservice.schedule.domain.model.ScheduleId;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepositoryPort {
    Schedule save(Schedule schedule);
    Optional<Schedule> findById(ScheduleId scheduleId);
    Optional<Schedule> findByClassSessionId(String classSessionId);
    List<Schedule> findAll();
    void deleteById(ScheduleId scheduleId);
}
