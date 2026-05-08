package com.andre_nathan.gym_webservice.enrollment.infrastructure.acl;

import com.andre_nathan.gym_webservice.enrollment.application.port.out.SchedulePort;
import com.andre_nathan.gym_webservice.enrollment.infrastructure.config.RestTemplateConfig;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(
        value = ScheduleAclAdapter.class,
        properties = "services.schedule.base-url=http://schedule-service.test"
)
@Import(RestTemplateConfig.class)
@ActiveProfiles("testing")
class ScheduleAclAdapterTest {

    @Autowired
    private ScheduleAclAdapter adapter;

    @Autowired
    private MockRestServiceServer server;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = (RestTemplate) ReflectionTestUtils.getField(adapter, "restTemplate");
        assertNotNull(restTemplate);
        server = MockRestServiceServer.bindTo(restTemplate).build();
    }

    @Test
    void findByClassSessionIdCallsExpectedUrlAndMapsResponse() {
        server.expect(requestTo("http://schedule-service.test/api/schedules/class-sessions/session-1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(scheduleJson("schedule-1", "session-1", "trainer-1", 10, 1), MediaType.APPLICATION_JSON));

        var schedule = adapter.findByClassSessionId("session-1").orElseThrow();

        assertEquals("schedule-1", schedule.scheduleId());
        assertEquals("session-1", schedule.classSessionId());
        assertTrue(schedule.hasAvailableSeat());
        server.verify();
    }

    @Test
    void findByIdNotFoundReturnsEmpty() {
        server.expect(requestTo("http://schedule-service.test/api/schedules/missing"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertTrue(adapter.findById("missing").isEmpty());
        server.verify();
    }

    @Test
    void saveUsesPutAndMapsResponse() {
        SchedulePort.ScheduleSnapshot schedule = new SchedulePort.ScheduleSnapshot(
                "schedule-1",
                "Morning Yoga",
                "Yoga",
                LocalDateTime.of(2030, 1, 1, 9, 0),
                LocalDateTime.of(2030, 1, 1, 10, 0),
                "trainer-1",
                10,
                2,
                "room-1",
                "Studio A",
                20,
                "session-1",
                LocalDate.of(2030, 1, 1),
                "SCHEDULED"
        );

        server.expect(requestTo("http://schedule-service.test/api/schedules/schedule-1"))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(content().string(Matchers.containsString("\"enrolledCount\":2")))
                .andRespond(withSuccess(scheduleJson("schedule-1", "session-1", "trainer-1", 10, 2), MediaType.APPLICATION_JSON));

        SchedulePort.ScheduleSnapshot updated = adapter.save(schedule);

        assertEquals(2, updated.enrolledCount());
        server.verify();
    }

    @Test
    void missingNestedFieldsThrowsIllegalState() {
        server.expect(requestTo("http://schedule-service.test/api/schedules/schedule-1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {
                          "scheduleId": "schedule-1",
                          "className": "Morning Yoga",
                          "classType": "Yoga",
                          "startTime": "2030-01-01T09:00:00",
                          "endTime": "2030-01-01T10:00:00",
                          "trainerId": "trainer-1",
                          "maxCapacity": 10,
                          "enrolledCount": 1
                        }
                        """, MediaType.APPLICATION_JSON));

        assertThrows(IllegalStateException.class, () -> adapter.findById("schedule-1"));
        server.verify();
    }

    @Test
    void serverErrorIsPropagated() {
        server.expect(requestTo("http://schedule-service.test/api/schedules/class-sessions/session-1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrows(HttpServerErrorException.class, () -> adapter.findByClassSessionId("session-1"));
        server.verify();
    }

    private String scheduleJson(String scheduleId, String classSessionId, String trainerId, int maxCapacity, int enrolledCount) {
        return """
                {
                  "scheduleId": "%s",
                  "className": "Morning Yoga",
                  "classType": "Yoga",
                  "startTime": "2030-01-01T09:00:00",
                  "endTime": "2030-01-01T10:00:00",
                  "trainerId": "%s",
                  "maxCapacity": %d,
                  "enrolledCount": %d,
                  "room": {
                    "roomId": "room-1",
                    "roomName": "Studio A",
                    "roomCapacity": 20
                  },
                  "classSession": {
                    "classSessionId": "%s",
                    "sessionDate": "2030-01-01",
                    "sessionStatus": "SCHEDULED"
                  }
                }
                """.formatted(scheduleId, trainerId, maxCapacity, enrolledCount, classSessionId);
    }
}
