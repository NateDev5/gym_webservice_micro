package com.andre_nathan.gym_webservice.member.api;

import com.andre_nathan.gym_webservice.member.api.dto.CreateMemberRequest;
import com.andre_nathan.gym_webservice.member.api.dto.UpdateMemberRequest;
import com.andre_nathan.gym_webservice.member.api.mapper.MemberApiMapper;
import com.andre_nathan.gym_webservice.member.application.service.MemberCrudService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/members")
@Tag(name = "Member")
public class MemberController {
    private final MemberCrudService service;
    public MemberController(MemberCrudService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Create member")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Member created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "409", description = "Duplicate member")
    })
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
    @Operation(summary = "Get member by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Member found"),
            @ApiResponse(responseCode = "404", description = "Member not found")
    })
    public ResponseEntity<?> get(@PathVariable String id) {
        return ResponseEntity.ok(MemberApiMapper.toResponse(service.getById(id)));
    }

    @GetMapping
    @Operation(summary = "List all members")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Members returned")
    })
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(
                service.getAll().stream()
                        .map(MemberApiMapper::toResponse)
                        .collect(Collectors.toList())
        );
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update member")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Member updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Member not found"),
            @ApiResponse(responseCode = "409", description = "Duplicate member")
    })
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
    @Operation(summary = "Delete member")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Member deleted"),
            @ApiResponse(responseCode = "404", description = "Member not found")
    })
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
