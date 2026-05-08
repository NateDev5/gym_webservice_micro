package com.andre_nathan.gym_webservice.schedule.api;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("testing")
class ScheduleControllerIntegrationTest {

    private static final String SCHEDULES_ENDPOINT = "/api/schedules";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createGetUpdateDeleteHappyPath() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        String classSessionId = "session-" + suffix;
        String payload = validPayload(
                "room-" + suffix,
                "trainer-" + suffix,
                classSessionId,
                LocalDateTime.of(2030, 1, 10, 9, 0),
                LocalDateTime.of(2030, 1, 10, 10, 0),
                "Morning Yoga"
        );

        String createResponse = mockMvc.perform(post(SCHEDULES_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.classSession.classSessionId").value(classSessionId))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String scheduleId = extractField(createResponse, "scheduleId");

        mockMvc.perform(get(SCHEDULES_ENDPOINT + "/{id}", scheduleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scheduleId").value(scheduleId));

        mockMvc.perform(get(SCHEDULES_ENDPOINT + "/class-sessions/{id}", classSessionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.classSession.classSessionId").value(classSessionId));

        String updatePayload = validPayload(
                "room-updated-" + suffix,
                "trainer-updated-" + suffix,
                classSessionId,
                LocalDateTime.of(2030, 1, 10, 10, 30),
                LocalDateTime.of(2030, 1, 10, 11, 30),
                "Updated Class"
        );

        mockMvc.perform(put(SCHEDULES_ENDPOINT + "/{id}", scheduleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.className").value("Updated Class"));

        mockMvc.perform(delete(SCHEDULES_ENDPOINT + "/{id}", scheduleId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(SCHEDULES_ENDPOINT + "/{id}", scheduleId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllHappyPath() throws Exception {
        mockMvc.perform(get(SCHEDULES_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void invalidCreateRequestReturnsBadRequest() throws Exception {
        String payload = validPayload(
                "room-a",
                "trainer-a",
                "session-a",
                LocalDateTime.of(2030, 2, 1, 9, 0),
                LocalDateTime.of(2030, 2, 1, 10, 0),
                ""
        );

        mockMvc.perform(post(SCHEDULES_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void missingResourceReturnsNotFound() throws Exception {
        mockMvc.perform(get(SCHEDULES_ENDPOINT + "/{id}", "missing-schedule-id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void overlappingRoomReturnsConflict() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        String roomId = "room-conflict-" + suffix;
        String trainerA = "trainer-a-" + suffix;
        String trainerB = "trainer-b-" + suffix;

        String first = validPayload(
                roomId,
                trainerA,
                "session-first-" + suffix,
                LocalDateTime.of(2030, 3, 1, 9, 0),
                LocalDateTime.of(2030, 3, 1, 10, 0),
                "Morning Yoga"
        );
        String second = validPayload(
                roomId,
                trainerB,
                "session-second-" + suffix,
                LocalDateTime.of(2030, 3, 1, 9, 30),
                LocalDateTime.of(2030, 3, 1, 10, 30),
                "Morning Yoga"
        );

        mockMvc.perform(post(SCHEDULES_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(first))
                .andExpect(status().isCreated());

        mockMvc.perform(post(SCHEDULES_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(second))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void malformedJsonReturnsBadRequest() throws Exception {
        mockMvc.perform(post(SCHEDULES_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid-json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    private String validPayload(
            String roomId,
            String trainerId,
            String classSessionId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            String className
    ) {
        return """
                {
                  "className": "%s",
                  "classType": "Yoga",
                  "startTime": "%s",
                  "endTime": "%s",
                  "roomId": "%s",
                  "roomName": "Studio A",
                  "roomCapacity": 20,
                  "trainerId": "%s",
                  "maxCapacity": 20,
                  "enrolledCount": 0,
                  "classSessionId": "%s",
                  "sessionDate": "%s",
                  "sessionStatus": "SCHEDULED"
                }
                """.formatted(
                className,
                startTime,
                endTime,
                roomId,
                trainerId,
                classSessionId,
                LocalDate.from(startTime)
        );
    }

    private String extractField(String json, String fieldName) {
        Pattern pattern = Pattern.compile("\"" + fieldName + "\"\\s*:\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(json);
        if (!matcher.find()) {
            throw new IllegalStateException("Field not found in response: " + fieldName);
        }
        return matcher.group(1);
    }
}
