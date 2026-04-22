package com.andre_nathan.gym_webservice.member.api;

import com.andre_nathan.gym_webservice.member.api.dto.CreateMemberRequest;
import com.andre_nathan.gym_webservice.member.api.dto.UpdateMemberRequest;
import com.andre_nathan.gym_webservice.member.api.mapper.MemberApiMapper;
import com.andre_nathan.gym_webservice.member.application.service.MemberCrudService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/members")
public class MemberController {
    private final MemberCrudService service;
    public MemberController(MemberCrudService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid CreateMemberRequest createMemberRequest) {
        var member = service.create(
                createMemberRequest.fullName(),
                createMemberRequest.dateOfBirth(),
                createMemberRequest.email(),
                createMemberRequest.phone(),
                createMemberRequest.membershipPlanId(),
                createMemberRequest.membershipStatus(),
                createMemberRequest.membershipStartDate(),
                createMemberRequest.membershipEndDate()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(MemberApiMapper.toResponse(member));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable String id) {
        return ResponseEntity.ok(MemberApiMapper.toResponse(service.getById(id)));
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(
                service.getAll().stream()
                        .map(MemberApiMapper::toResponse)
                        .collect(Collectors.toList())
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody @Valid UpdateMemberRequest updateMemberRequest) {
        var member = service.update(
                id,
                updateMemberRequest.fullName(),
                updateMemberRequest.dateOfBirth(),
                updateMemberRequest.email(),
                updateMemberRequest.phone(),
                updateMemberRequest.membershipPlanId(),
                updateMemberRequest.membershipStatus(),
                updateMemberRequest.membershipStartDate(),
                updateMemberRequest.membershipEndDate()
        );
        return ResponseEntity.ok(MemberApiMapper.toResponse(member));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
