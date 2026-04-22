package com.andre_nathan.gym_webservice.enrollment.infrastructure.acl;

import com.andre_nathan.gym_webservice.enrollment.application.port.out.SchedulePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class ScheduleAclAdapter implements SchedulePort {
    private final RestTemplate restTemplate;

    @Value("${services.schedule.base-url}")
    private String scheduleServiceBaseUrl;

    public ScheduleAclAdapter(
            RestTemplate restTemplate
    ) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Optional<ScheduleSnapshot> findById(String scheduleId) {
        try {
            ResponseEntity<ScheduleApiResponse> response = restTemplate.getForEntity(
                    scheduleServiceBaseUrl + "/api/schedules/{id}",
                    ScheduleApiResponse.class,
                    scheduleId
            );
            return Optional.ofNullable(response.getBody()).map(this::toSnapshot);
        } catch (HttpClientErrorException.NotFound ex) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<ScheduleSnapshot> findByClassSessionId(String classSessionId) {
        try {
            ResponseEntity<ScheduleApiResponse> response = restTemplate.getForEntity(
                    scheduleServiceBaseUrl + "/api/schedules/class-sessions/{classSessionId}",
                    ScheduleApiResponse.class,
                    classSessionId
            );
            return Optional.ofNullable(response.getBody()).map(this::toSnapshot);
        } catch (HttpClientErrorException.NotFound ex) {
            return Optional.empty();
        }
    }

    @Override
    public ScheduleSnapshot save(ScheduleSnapshot schedule) {
        UpdateScheduleApiRequest requestBody = new UpdateScheduleApiRequest(
                schedule.className(),
                schedule.classType(),
                schedule.startTime(),
                schedule.endTime(),
                schedule.roomId(),
                schedule.roomName(),
                schedule.roomCapacity(),
                schedule.trainerId(),
                schedule.maxCapacity(),
                schedule.enrolledCount(),
                schedule.classSessionId(),
                schedule.sessionDate(),
                schedule.sessionStatus()
        );

        ResponseEntity<ScheduleApiResponse> response = restTemplate.exchange(
                scheduleServiceBaseUrl + "/api/schedules/{id}",
                HttpMethod.PUT,
                new HttpEntity<>(requestBody),
                ScheduleApiResponse.class,
                schedule.scheduleId()
        );

        ScheduleApiResponse body = response.getBody();
        if (body == null) {
            throw new IllegalStateException("Schedule service returned empty response body on update");
        }

        return toSnapshot(body);
    }

    private ScheduleSnapshot toSnapshot(ScheduleApiResponse response) {
        if (response.room == null || response.classSession == null) {
            throw new IllegalStateException("Schedule response is missing room or classSession data");
        }

        return new ScheduleSnapshot(
                response.scheduleId,
                response.className,
                response.classType,
                response.startTime,
                response.endTime,
                response.trainerId,
                response.maxCapacity,
                response.enrolledCount,
                response.room.roomId,
                response.room.roomName,
                response.room.roomCapacity,
                response.classSession.classSessionId,
                response.classSession.sessionDate,
                response.classSession.sessionStatus
        );
    }

    private record UpdateScheduleApiRequest(
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
    }

    private static class ScheduleApiResponse {
        public String scheduleId;
        public String className;
        public String classType;
        public LocalDateTime startTime;
        public LocalDateTime endTime;
        public String trainerId;
        public int maxCapacity;
        public int enrolledCount;
        public RoomApiResponse room;
        public ClassSessionApiResponse classSession;
    }

    private static class RoomApiResponse {
        public String roomId;
        public String roomName;
        public int roomCapacity;
    }

    private static class ClassSessionApiResponse {
        public String classSessionId;
        public LocalDate sessionDate;
        public String sessionStatus;
    }
}
