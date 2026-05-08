package com.andre_nathan.gym_webservice.member.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("testing")
class MemberRepositoryIntegrationTest {

    @Autowired
    private SpringDataMemberRepository memberRepository;

    @Autowired
    private SpringDataMembershipPlanRepository membershipPlanRepository;

    private UUID membershipPlanId;

    @BeforeEach
    void setUp() {
        memberRepository.deleteAll();
        membershipPlanRepository.deleteAll();

        MembershipPlanJpaEntity plan = new MembershipPlanJpaEntity();
        membershipPlanId = UUID.randomUUID();
        plan.planId = membershipPlanId;
        plan.planName = "Test Plan";
        plan.durationInMonths = 12;
        plan.price = new BigDecimal("199.99");
        membershipPlanRepository.save(plan);
    }

    @Test
    void savesAndFindsById() {
        MemberJpaEntity saved = memberRepository.save(newMember("member-1", "member.one@example.com"));

        assertTrue(memberRepository.findById(saved.memberId).isPresent());
    }

    @Test
    void findsByMeaningfulFieldAndExistsChecks() {
        memberRepository.save(newMember("member-2", "member.two@example.com"));

        assertTrue(memberRepository.existsByEmail("member.two@example.com"));
        assertFalse(memberRepository.existsByEmail("missing@example.com"));
        assertTrue(memberRepository.existsByEmailAndMemberIdNot("member.two@example.com", "other-id"));
        assertFalse(memberRepository.existsByEmailAndMemberIdNot("member.two@example.com", "member-2"));
    }

    @Test
    void deleteAndMissingReturnEmptyOrFalse() {
        memberRepository.save(newMember("member-3", "member.three@example.com"));

        memberRepository.deleteById("member-3");

        assertTrue(memberRepository.findById("member-3").isEmpty());
        assertFalse(memberRepository.existsByEmail("member.three@example.com"));
    }

    private MemberJpaEntity newMember(String id, String email) {
        MemberJpaEntity entity = new MemberJpaEntity();
        entity.memberId = id;
        entity.fullName = "Test Member";
        entity.dateOfBirth = LocalDate.of(1990, 1, 1);
        entity.email = email;
        entity.phone = "+15145550000";
        entity.membershipPlanId = membershipPlanId;
        entity.membershipStatus = "ACTIVE";
        entity.membershipStartDate = LocalDate.now();
        entity.membershipEndDate = LocalDate.now().plusMonths(3);
        return entity;
    }
}
