package com.andre_nathan.gym_webservice.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("testing")
class ApiGatewayRoutingTest {

    @Autowired
    private RouteLocator routeLocator;

    @LocalServerPort
    private int port;

    @Test
    void allExpectedRoutesExistWithExpectedUris() {
        var routeList = routeLocator.getRoutes().collectList().block();
        assertNotNull(routeList);

        Map<String, String> routes = routeList
                .stream()
                .collect(Collectors.toMap(Route::getId, route -> route.getUri().toString(), (a, b) -> a));

        assertAll(
                () -> assertRoute(routes, "enrollment-service", "http://localhost:8081"),
                () -> assertRoute(routes, "member-service", "http://localhost:8082"),
                () -> assertRoute(routes, "schedule-service", "http://localhost:8083"),
                () -> assertRoute(routes, "trainer-service", "http://localhost:8084"),
                () -> assertRoute(routes, "enrollment-service-docs", "http://localhost:8081"),
                () -> assertRoute(routes, "member-service-docs", "http://localhost:8082"),
                () -> assertRoute(routes, "schedule-service-docs", "http://localhost:8083"),
                () -> assertRoute(routes, "trainer-service-docs", "http://localhost:8084")
        );
    }

    @Test
    void unknownRouteReturnsNotFound() {
        webTestClient().get()
                .uri("/api/unknown-route")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void actuatorHealthEndpointResponds() {
        webTestClient().get()
                .uri("/actuator/health")
                .exchange()
                .expectStatus()
                .value(status -> assertTrue(status == 200 || status == 404));
    }

    private WebTestClient webTestClient() {
        return WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
    }

    private void assertRoute(Map<String, String> routes, String routeId, String expectedUriPrefix) {
        assertTrue(routes.containsKey(routeId), () -> "Missing route id: " + routeId);
        assertTrue(routes.get(routeId).startsWith(expectedUriPrefix),
                () -> "Unexpected URI for route " + routeId + ": " + routes.get(routeId));
    }
}
