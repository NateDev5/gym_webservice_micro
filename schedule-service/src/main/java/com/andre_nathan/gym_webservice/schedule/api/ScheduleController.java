package com.andre_nathan.gym_webservice.schedule.api;

import com.andre_nathan.gym_webservice.schedule.api.dto.CreateScheduleRequest;
import com.andre_nathan.gym_webservice.schedule.api.dto.UpdateScheduleRequest;
import com.andre_nathan.gym_webservice.schedule.api.mapper.ScheduleApiMapper;
import com.andre_nathan.gym_webservice.schedule.application.service.ScheduleCrudService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Create schedule")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Schedule created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "409", description = "Schedule conflict")
    })
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
    @Operation(summary = "Get schedule by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Schedule found"),
            @ApiResponse(responseCode = "404", description = "Schedule not found")
    })
    public ResponseEntity<?> getById(@PathVariable String id) {
        return ResponseEntity.ok(ScheduleApiMapper.toResponse(service.getById(id)));
    }

    @GetMapping
    @Operation(summary = "List all schedules")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Schedules returned")
    })
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(service.getAll().stream().map(ScheduleApiMapper::toResponse).toList());
    }

    @GetMapping("/class-sessions/{classSessionId}")
    @Operation(summary = "Get schedule by class session id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Schedule found"),
            @ApiResponse(responseCode = "404", description = "Class session not found")
    })
    public ResponseEntity<?> getByClassSessionId(@PathVariable String classSessionId) {
        return ResponseEntity.ok(ScheduleApiMapper.toResponse(service.getByClassSessionId(classSessionId)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update schedule")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Schedule updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Schedule not found"),
            @ApiResponse(responseCode = "409", description = "Schedule conflict")
    })
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
    @Operation(summary = "Delete schedule")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Schedule deleted"),
            @ApiResponse(responseCode = "404", description = "Schedule not found")
    })
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
