package com.andre_nathan.gym_webservice.schedule.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataScheduleRepository extends JpaRepository<ScheduleJpaEntity, String> {
    Optional<ScheduleJpaEntity> findByClassSessionId(String classSessionId);
}
