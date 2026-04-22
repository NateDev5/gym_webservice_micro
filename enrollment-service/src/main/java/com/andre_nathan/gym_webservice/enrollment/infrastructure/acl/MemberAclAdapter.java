package com.andre_nathan.gym_webservice.enrollment.infrastructure.acl;

import com.andre_nathan.gym_webservice.enrollment.application.port.out.MemberPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class MemberAclAdapter implements MemberPort {
    private final RestTemplate restTemplate;

    @Value("${services.member.base-url}")
    private String memberServiceBaseUrl;

    public MemberAclAdapter(
            RestTemplate restTemplate
    ) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Optional<MemberSnapshot> findById(String memberId) {
        try {
            ResponseEntity<MemberApiResponse> response = restTemplate.getForEntity(
                    memberServiceBaseUrl + "/api/members/{id}",
                    MemberApiResponse.class,
                    memberId
            );
            return Optional.ofNullable(response.getBody()).map(this::toSnapshot);
        } catch (HttpClientErrorException.NotFound ex) {
            return Optional.empty();
        }
    }

    private MemberSnapshot toSnapshot(MemberApiResponse response) {
        return new MemberSnapshot(
                response.memberId,
                response.fullName,
                response.membershipStatus,
                response.membershipEndDate
        );
    }

    private static class MemberApiResponse {
        public String memberId;
        public String fullName;
        public String membershipStatus;
        public LocalDate membershipEndDate;
    }
}
