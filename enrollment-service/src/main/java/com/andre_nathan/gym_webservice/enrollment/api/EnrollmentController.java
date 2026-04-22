package com.andre_nathan.gym_webservice.enrollment.api;

import com.andre_nathan.gym_webservice.enrollment.api.dto.CancelEnrollmentRequest;
import com.andre_nathan.gym_webservice.enrollment.api.dto.EnrollClassRequest;
import com.andre_nathan.gym_webservice.enrollment.api.dto.EnrollmentResponse;
import com.andre_nathan.gym_webservice.enrollment.api.dto.UpdateEnrollmentRequest;
import com.andre_nathan.gym_webservice.enrollment.application.service.EnrollmentCrudService;
import com.andre_nathan.gym_webservice.enrollment.application.service.EnrollmentOrchestrator;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {
    private final EnrollmentCrudService crudService;
    private final EnrollmentOrchestrator orchestrator;
    private final EnrollmentRepresentationAssembler assembler;

    public EnrollmentController(
            EnrollmentCrudService crudService,
            EnrollmentOrchestrator orchestrator,
            EnrollmentRepresentationAssembler assembler
    ) {
        this.crudService = crudService;
        this.orchestrator = orchestrator;
        this.assembler = assembler;
    }

    @PostMapping("/enroll")
    public ResponseEntity<EnrollmentResponse> enrollInClass(@RequestBody @Valid EnrollClassRequest request) {
        var enrollment = orchestrator.enrollMemberInClass(
                request.memberId(),
                request.classSessionId(),
                request.trainerId(),
                request.scheduleId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(enrollment));
    }

    @PostMapping("/cancel")
    public ResponseEntity<EnrollmentResponse> cancelEnrollment(@RequestBody @Valid CancelEnrollmentRequest request) {
        var enrollment = orchestrator.cancelEnrollment(
                request.memberId(),
                request.classSessionId()
        );
        return ResponseEntity.ok(assembler.toModel(enrollment));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentResponse> getById(@PathVariable String id) {
        var enrollment = crudService.getById(id);
        return ResponseEntity.ok(assembler.toModel(enrollment));
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EnrollmentResponse>> getAll() {
        var enrollments = crudService.getAll();
        var responses = enrollments.stream()
                .map(assembler::toModel)
                .toList();

        var collectionModel = CollectionModel.of(responses);
        collectionModel.add(linkTo(methodOn(EnrollmentController.class).getAll()).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EnrollmentResponse> update(@PathVariable String id, @RequestBody @Valid UpdateEnrollmentRequest request) {
        var existing = crudService.getById(id);
        var updated = crudService.update(id, request.memberId(), existing.getRegisteredClasses());
        return ResponseEntity.ok(assembler.toModel(updated));
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<CollectionModel<EnrollmentResponse>> getByMemberId(@PathVariable String memberId) {
        var enrollments = crudService.getAllForMember(memberId);
        var responses = enrollments.stream()
                .map(assembler::toModel)
                .toList();

        var collectionModel = CollectionModel.of(responses);
        collectionModel.add(linkTo(methodOn(EnrollmentController.class).getByMemberId(memberId)).withSelfRel());
        collectionModel.add(linkTo(methodOn(EnrollmentController.class).getAll()).withRel("all-enrollments"));

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/schedule/{scheduleId}")
    public ResponseEntity<CollectionModel<EnrollmentResponse>> getByScheduleId(@PathVariable String scheduleId) {
        var enrollments = crudService.getAllForSchedule(scheduleId);
        var responses = enrollments.stream()
                .map(assembler::toModel)
                .toList();

        var collectionModel = CollectionModel.of(responses);
        collectionModel.add(linkTo(methodOn(EnrollmentController.class).getByScheduleId(scheduleId)).withSelfRel());
        collectionModel.add(linkTo(methodOn(EnrollmentController.class).getAll()).withRel("all-enrollments"));

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/trainer/{trainerId}")
    public ResponseEntity<CollectionModel<EnrollmentResponse>> getByTrainerId(@PathVariable String trainerId) {
        var enrollments = crudService.getAllForTrainer(trainerId);
        var responses = enrollments.stream()
                .map(assembler::toModel)
                .toList();

        var collectionModel = CollectionModel.of(responses);
        collectionModel.add(linkTo(methodOn(EnrollmentController.class).getByTrainerId(trainerId)).withSelfRel());
        collectionModel.add(linkTo(methodOn(EnrollmentController.class).getAll()).withRel("all-enrollments"));

        return ResponseEntity.ok(collectionModel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        crudService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
