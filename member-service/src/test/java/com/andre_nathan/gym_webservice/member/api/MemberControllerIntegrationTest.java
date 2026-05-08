package com.andre_nathan.gym_webservice.member.api;
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
class MemberControllerIntegrationTest {

    private static final String MEMBERS_ENDPOINT = "/api/members";
    private static final String MEMBERSHIP_PLAN_ID = "cab08990-8bbd-4210-b905-ff3ab3665622";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createGetUpdateDeleteHappyPath() throws Exception {
        String email = "member." + UUID.randomUUID() + "@example.com";
        String createPayload = validPayload(email, "Test Member", "ACTIVE");

        String createResponse = mockMvc.perform(post(MEMBERS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(email))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String memberId = extractField(createResponse, "memberId");

        mockMvc.perform(get(MEMBERS_ENDPOINT + "/{id}", memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(memberId));

        String updatePayload = validPayload(
                "updated." + UUID.randomUUID() + "@example.com",
                "Updated Member",
                "SUSPENDED"
        );

        mockMvc.perform(put(MEMBERS_ENDPOINT + "/{id}", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Updated Member"))
                .andExpect(jsonPath("$.membershipStatus").value("SUSPENDED"));

        mockMvc.perform(delete(MEMBERS_ENDPOINT + "/{id}", memberId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(MEMBERS_ENDPOINT + "/{id}", memberId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllHappyPath() throws Exception {
        mockMvc.perform(get(MEMBERS_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void invalidCreateRequestReturnsBadRequest() throws Exception {
        String payload = """
                {
                  "fullName": "",
                  "dateOfBirth": "1992-05-10",
                  "email": "invalid.%s@example.com",
                  "phone": "+15145554444",
                  "membershipPlanId": "%s",
                  "membershipStatus": "ACTIVE",
                  "membershipStartDate": "2026-01-01",
                  "membershipEndDate": "2026-12-31"
                }
                """.formatted(UUID.randomUUID(), MEMBERSHIP_PLAN_ID);

        mockMvc.perform(post(MEMBERS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void missingResourceReturnsNotFound() throws Exception {
        mockMvc.perform(get(MEMBERS_ENDPOINT + "/{id}", "missing-member-id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void duplicateCreateReturnsConflict() throws Exception {
        String email = "dup." + UUID.randomUUID() + "@example.com";
        String json = validPayload(email, "Test Member", "ACTIVE");

        mockMvc.perform(post(MEMBERS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        mockMvc.perform(post(MEMBERS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void malformedJsonReturnsBadRequest() throws Exception {
        mockMvc.perform(post(MEMBERS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid-json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    private String validPayload(String email, String fullName, String membershipStatus) {
        return """
                {
                  "fullName": "%s",
                  "dateOfBirth": "1992-05-10",
                  "email": "%s",
                  "phone": "+15145554444",
                  "membershipPlanId": "%s",
                  "membershipStatus": "%s",
                  "membershipStartDate": "2026-01-01",
                  "membershipEndDate": "2026-12-31"
                }
                """.formatted(fullName, email, MEMBERSHIP_PLAN_ID, membershipStatus);
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
