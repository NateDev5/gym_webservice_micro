package com.andre_nathan.gym_webservice.member.persistence;

import com.andre_nathan.gym_webservice.member.application.port.out.MembershipPlanRepositoryPort;
import com.andre_nathan.gym_webservice.member.domain.model.MembershipPlan;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class JpaMembershipPlanRepositoryAdapter implements MembershipPlanRepositoryPort {
    private final SpringDataMembershipPlanRepository jpa;

    public JpaMembershipPlanRepositoryAdapter(SpringDataMembershipPlanRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<MembershipPlan> findById(UUID planId) {
        return jpa.findById(planId).map(this::toDomain);
    }

    private MembershipPlan toDomain(MembershipPlanJpaEntity entity) {
        return new MembershipPlan(
                entity.planId,
                entity.planName,
                entity.durationInMonths,
                entity.price
        );
    }
}
