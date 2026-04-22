package com.andre_nathan.gym_webservice.trainer.api;

import com.andre_nathan.gym_webservice.trainer.api.dto.CreateTrainerRequest;
import com.andre_nathan.gym_webservice.trainer.api.dto.UpdateTrainerRequest;
import com.andre_nathan.gym_webservice.trainer.api.mapper.TrainerApiMapper;
import com.andre_nathan.gym_webservice.trainer.application.service.TrainerCrudService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trainers")
@Tag(name = "Trainer")
public class TrainerController {
    private final TrainerCrudService service;

    public TrainerController(TrainerCrudService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Create trainer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Trainer created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "409", description = "Duplicate trainer")
    })
    public ResponseEntity<?> create(@RequestBody @Valid CreateTrainerRequest request) {
        var trainer = service.create(
                request.fullName(),
                request.email(),
                request.specialty(),
                request.active(),
                request.certifications()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(TrainerApiMapper.toResponse(trainer));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get trainer by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer found"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    public ResponseEntity<?> getById(@PathVariable String id) {
        return ResponseEntity.ok(TrainerApiMapper.toResponse(service.getById(id)));
    }

    @GetMapping
    @Operation(summary = "List all trainers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainers returned")
    })
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(service.getAll().stream().map(TrainerApiMapper::toResponse).toList());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update trainer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Trainer not found"),
            @ApiResponse(responseCode = "409", description = "Duplicate trainer")
    })
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody @Valid UpdateTrainerRequest request) {
        var trainer = service.update(
                id,
                request.fullName(),
                request.email(),
                request.specialty(),
                request.active(),
                request.certifications()
        );
        return ResponseEntity.ok(TrainerApiMapper.toResponse(trainer));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete trainer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Trainer deleted"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
