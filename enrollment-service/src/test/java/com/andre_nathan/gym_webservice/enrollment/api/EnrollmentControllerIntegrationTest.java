package com.andre_nathan.gym_webservice.enrollment.api;

import com.andre_nathan.gym_webservice.enrollment.api.dto.EnrollmentResponse;
import com.andre_nathan.gym_webservice.enrollment.application.exception.EnrollmentNotFoundException;
import com.andre_nathan.gym_webservice.enrollment.application.exception.InvalidMembershipException;
import com.andre_nathan.gym_webservice.enrollment.application.service.EnrollmentCrudService;
import com.andre_nathan.gym_webservice.enrollment.application.service.EnrollmentOrchestrator;
import com.andre_nathan.gym_webservice.enrollment.domain.model.Enrollment;
import com.andre_nathan.gym_webservice.enrollment.domain.model.EnrollmentId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("testing")
class EnrollmentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EnrollmentCrudService crudService;

    @MockitoBean
    private EnrollmentOrchestrator orchestrator;

    @MockitoBean
    private EnrollmentRepresentationAssembler assembler;

    @Test
    void enrollAndCancelHappyPath() throws Exception {
        Enrollment enrollment = enrollment("enrollment-1", "member-1");
        EnrollmentResponse response = response("enrollment-1", "member-1");
        when(orchestrator.enrollMemberInClass("member-1", "session-1", "trainer-1", "schedule-1")).thenReturn(enrollment);
        when(orchestrator.cancelEnrollment("member-1", "session-1")).thenReturn(enrollment);
        when(assembler.toModel(enrollment)).thenReturn(response);

        mockMvc.perform(post("/api/enrollments/enroll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(enrollPayload("member-1", "session-1", "trainer-1", "schedule-1")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.memberId").value("member-1"));

        mockMvc.perform(post("/api/enrollments/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cancelPayload("member-1", "session-1")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value("member-1"));
    }

    @Test
    void getUpdateDeleteHappyPath() throws Exception {
        Enrollment existing = enrollment("enrollment-2", "member-2");
        Enrollment updated = enrollment("enrollment-2", "member-3");
        when(crudService.getById("enrollment-2")).thenReturn(existing);
        when(crudService.update(eq("enrollment-2"), eq("member-3"), anyList())).thenReturn(updated);
        when(assembler.toModel(existing)).thenReturn(response("enrollment-2", "member-2"));
        when(assembler.toModel(updated)).thenReturn(response("enrollment-2", "member-3"));

        mockMvc.perform(get("/api/enrollments/{id}", "enrollment-2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value("member-2"));

        mockMvc.perform(put("/api/enrollments/{id}", "enrollment-2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"memberId\":\"member-3\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value("member-3"));

        mockMvc.perform(delete("/api/enrollments/{id}", "enrollment-2"))
                .andExpect(status().isNoContent());
    }

    @Test
    void listEndpointsHappyPath() throws Exception {
        Enrollment enrollment = enrollment("enrollment-1", "member-1");
        EnrollmentResponse response = response("enrollment-1", "member-1");
        when(crudService.getAll()).thenReturn(List.of(enrollment));
        when(crudService.getAllForMember("member-1")).thenReturn(List.of(enrollment));
        when(crudService.getAllForSchedule("schedule-1")).thenReturn(List.of(enrollment));
        when(crudService.getAllForTrainer("trainer-1")).thenReturn(List.of(enrollment));
        when(assembler.toModel(enrollment)).thenReturn(response);

        mockMvc.perform(get("/api/enrollments"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/enrollments/member/{memberId}", "member-1"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/enrollments/schedule/{scheduleId}", "schedule-1"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/enrollments/trainer/{trainerId}", "trainer-1"))
                .andExpect(status().isOk());
    }

    @Test
    void invalidAndMalformedRequestsReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/enrollments/enroll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"memberId\":\"\",\"classSessionId\":\"session-1\",\"trainerId\":\"trainer-1\",\"scheduleId\":\"schedule-1\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        mockMvc.perform(post("/api/enrollments/enroll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid-json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void exceptionPathsReturnExpectedStatuses() throws Exception {
        when(crudService.getById("missing")).thenThrow(new EnrollmentNotFoundException(EnrollmentId.of("missing")));
        when(orchestrator.enrollMemberInClass(anyString(), anyString(), anyString(), anyString()))
                .thenThrow(new InvalidMembershipException("member-1"));

        mockMvc.perform(get("/api/enrollments/{id}", "missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        mockMvc.perform(post("/api/enrollments/enroll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(enrollPayload("member-1", "session-1", "trainer-1", "schedule-1")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    private Enrollment enrollment(String enrollmentId, String memberId) {
        return new Enrollment(EnrollmentId.of(enrollmentId), memberId, List.of());
    }

    private EnrollmentResponse response(String enrollmentId, String memberId) {
        return new EnrollmentResponse(enrollmentId, memberId, "Member", "ACTIVE", List.of());
    }

    private String enrollPayload(String memberId, String classSessionId, String trainerId, String scheduleId) {
        return """
                {"memberId":"%s","classSessionId":"%s","trainerId":"%s","scheduleId":"%s"}
                """.formatted(memberId, classSessionId, trainerId, scheduleId);
    }

    private String cancelPayload(String memberId, String classSessionId) {
        return """
                {"memberId":"%s","classSessionId":"%s"}
                """.formatted(memberId, classSessionId);
    }
}
