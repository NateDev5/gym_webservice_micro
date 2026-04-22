package com.andre_nathan.gym_webservice.schedule.api;

import com.andre_nathan.gym_webservice.schedule.api.dto.CreateScheduleRequest;
import com.andre_nathan.gym_webservice.schedule.api.dto.UpdateScheduleRequest;
import com.andre_nathan.gym_webservice.schedule.api.mapper.ScheduleApiMapper;
import com.andre_nathan.gym_webservice.schedule.application.service.ScheduleCrudService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schedules")
@Tag(name = "Schedule")
public class ScheduleController {
    private final ScheduleCrudService service;

    public ScheduleController(ScheduleCrudService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid CreateScheduleRequest request) {
        var schedule = service.create(
                request.className(),
                request.classType(),
                request.startTime(),
                request.endTime(),
                request.roomId(),
                request.roomName(),
                request.roomCapacity(),
                request.trainerId(),
                request.maxCapacity(),
                request.enrolledCount(),
                request.classSessionId(),
                request.sessionDate(),
                request.sessionStatus()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ScheduleApiMapper.toResponse(schedule));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        return ResponseEntity.ok(ScheduleApiMapper.toResponse(service.getById(id)));
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(service.getAll().stream().map(ScheduleApiMapper::toResponse).toList());
    }

    @GetMapping("/class-sessions/{classSessionId}")
    public ResponseEntity<?> getByClassSessionId(@PathVariable String classSessionId) {
        return ResponseEntity.ok(ScheduleApiMapper.toResponse(service.getByClassSessionId(classSessionId)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody @Valid UpdateScheduleRequest request) {
        var schedule = service.update(
                id,
                request.className(),
                request.classType(),
                request.startTime(),
                request.endTime(),
                request.roomId(),
                request.roomName(),
                request.roomCapacity(),
                request.trainerId(),
                request.maxCapacity(),
                request.enrolledCount(),
                request.classSessionId(),
                request.sessionDate(),
                request.sessionStatus()
        );
        return ResponseEntity.ok(ScheduleApiMapper.toResponse(schedule));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
