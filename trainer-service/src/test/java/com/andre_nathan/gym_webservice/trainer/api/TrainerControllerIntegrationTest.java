package com.andre_nathan.gym_webservice.trainer.api;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("testing")
class TrainerControllerIntegrationTest {

    private static final String TRAINERS_ENDPOINT = "/api/trainers";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createGetUpdateDeleteHappyPath() throws Exception {
        String email = "trainer." + UUID.randomUUID() + "@example.com";
        String createPayload = validPayload(email, "Test Trainer", "Yoga");

        String createResponse = mockMvc.perform(post(TRAINERS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(email))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String trainerId = extractField(createResponse, "trainerId");

        mockMvc.perform(get(TRAINERS_ENDPOINT + "/{id}", trainerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainerId").value(trainerId));

        String updatePayload = validPayload("updated." + UUID.randomUUID() + "@example.com", "Updated Trainer", "Pilates");

        mockMvc.perform(put(TRAINERS_ENDPOINT + "/{id}", trainerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Updated Trainer"))
                .andExpect(jsonPath("$.specialty").value("Pilates"));

        mockMvc.perform(delete(TRAINERS_ENDPOINT + "/{id}", trainerId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(TRAINERS_ENDPOINT + "/{id}", trainerId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllHappyPath() throws Exception {
        mockMvc.perform(get(TRAINERS_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void invalidCreateRequestReturnsBadRequest() throws Exception {
        String payload = validPayload("invalid." + UUID.randomUUID() + "@example.com", "", "Yoga");

        mockMvc.perform(post(TRAINERS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void missingResourceReturnsNotFound() throws Exception {
        mockMvc.perform(get(TRAINERS_ENDPOINT + "/{id}", "missing-trainer-id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void duplicateCreateReturnsConflict() throws Exception {
        String email = "dup." + UUID.randomUUID() + "@example.com";
        String json = validPayload(email, "Test Trainer", "Yoga");

        mockMvc.perform(post(TRAINERS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        mockMvc.perform(post(TRAINERS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void malformedJsonReturnsBadRequest() throws Exception {
        mockMvc.perform(post(TRAINERS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid-json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    private String validPayload(String email, String fullName, String specialty) {
        return """
                {
                  "fullName": "%s",
                  "email": "%s",
                  "specialty": "%s",
                  "active": true,
                  "certifications": [
                    {
                      "certificateName": "Yoga Instructor",
                      "issuedDate": "2024-01-10",
                      "expiryDate": "2027-01-10"
                    }
                  ]
                }
                """.formatted(fullName, email, specialty);
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
