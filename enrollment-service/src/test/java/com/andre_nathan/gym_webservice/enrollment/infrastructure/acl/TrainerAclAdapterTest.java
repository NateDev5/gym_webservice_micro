package com.andre_nathan.gym_webservice.enrollment.infrastructure.acl;

import com.andre_nathan.gym_webservice.enrollment.infrastructure.config.RestTemplateConfig;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(
        value = TrainerAclAdapter.class,
        properties = "services.trainer.base-url=http://trainer-service.test"
)
@Import(RestTemplateConfig.class)
@ActiveProfiles("testing")
class TrainerAclAdapterTest {

    @Autowired
    private TrainerAclAdapter adapter;

    @Autowired
    private MockRestServiceServer server;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = (RestTemplate) ReflectionTestUtils.getField(adapter, "restTemplate");
        assertNotNull(restTemplate);
        server = MockRestServiceServer.bindTo(restTemplate).build();
    }

    @Test
    void findByIdCallsExpectedUrlAndMapsResponse() {
        server.expect(requestTo("http://trainer-service.test/api/trainers/trainer-1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {
                          "trainerId": "trainer-1",
                          "fullName": "Trainer One",
                          "specialty": "Yoga",
                          "active": true
                        }
                        """, MediaType.APPLICATION_JSON));

        var snapshot = adapter.findById("trainer-1").orElseThrow();

        assertEquals("trainer-1", snapshot.trainerId());
        assertTrue(snapshot.canTeach("Yoga"));
        server.verify();
    }

    @Test
    void notFoundReturnsEmpty() {
        server.expect(requestTo("http://trainer-service.test/api/trainers/missing"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertTrue(adapter.findById("missing").isEmpty());
        server.verify();
    }

    @Test
    void serverErrorIsPropagated() {
        server.expect(requestTo("http://trainer-service.test/api/trainers/trainer-1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrows(HttpServerErrorException.class, () -> adapter.findById("trainer-1"));
        server.verify();
    }
}
