package com.andre_nathan.gym_webservice.enrollment.infrastructure.acl;

import com.andre_nathan.gym_webservice.enrollment.application.port.out.TrainerPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Component
public class TrainerAclAdapter implements TrainerPort {
    private final RestTemplate restTemplate;

    @Value("${services.trainer.base-url}")
    private String trainerServiceBaseUrl;

    public TrainerAclAdapter(
            RestTemplate restTemplate
    ) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Optional<TrainerSnapshot> findById(String trainerId) {
        try {
            ResponseEntity<TrainerApiResponse> response = restTemplate.getForEntity(
                    trainerServiceBaseUrl + "/api/trainers/{id}",
                    TrainerApiResponse.class,
                    trainerId
            );
            return Optional.ofNullable(response.getBody()).map(this::toSnapshot);
        } catch (HttpClientErrorException.NotFound ex) {
            return Optional.empty();
        }
    }

    private TrainerSnapshot toSnapshot(TrainerApiResponse response) {
        return new TrainerSnapshot(
                response.trainerId,
                response.fullName,
                response.specialty,
                response.active
        );
    }

    private static class TrainerApiResponse {
        public String trainerId;
        public String fullName;
        public String specialty;
        public boolean active;
    }
}
