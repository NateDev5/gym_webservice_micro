package com.andre_nathan.gym_webservice.member.persistence;

import com.andre_nathan.gym_webservice.member.application.port.out.MemberRepositoryPort;
import com.andre_nathan.gym_webservice.member.domain.model.Member;
import com.andre_nathan.gym_webservice.member.domain.model.MemberId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class JpaMemberRepositoryAdapter implements MemberRepositoryPort {
    private final SpringDataMemberRepository jpa;
    private final SpringDataMembershipPlanRepository membershipPlanJpa;

    public JpaMemberRepositoryAdapter(
            SpringDataMemberRepository jpa,
            SpringDataMembershipPlanRepository membershipPlanJpa
    ) {
        this.jpa = jpa;
        this.membershipPlanJpa = membershipPlanJpa;
    }

    @Override
    public Member save(Member member) {
        jpa.save(toEntity(member));
        return member;
    }

    @Override
    public Optional<Member> findById(MemberId id) {
        return jpa.findById(id.value()).map(this::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpa.existsByEmail(email);
    }

    @Override
    public boolean existsByEmailExcludingId(String email, MemberId memberId) {
        return jpa.existsByEmailAndMemberIdNot(email, memberId.value());
    }

    @Override
    public List<Member> findAll() {
        return jpa.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteById(MemberId id) {
        jpa.deleteById(id.value());
    }

    private MemberJpaEntity toEntity(Member member) {
        var e = new MemberJpaEntity();
        e.memberId = member.getMemberId().value();
        e.fullName = member.getFullName().value();
        e.dateOfBirth = member.getDateOfBirth();
        e.email = member.getEmailAddress().value();
        e.phone = member.getPhoneNumber().value();
        e.membershipPlanId = member.getMembershipPlan().getPlanId();
        e.membershipStatus = member.getMembershipStatus().name();
        e.membershipStartDate = member.getMembershipStartDate();
        e.membershipEndDate = member.getMembershipEndDate();
        return e;
    }

    private Member toDomain (MemberJpaEntity entity) {
        MembershipPlanJpaEntity membershipPlanEntity = membershipPlanJpa.findById(entity.membershipPlanId)
                .orElseThrow(() -> new IllegalStateException("Membership plan not found for member: " + entity.memberId));

        return new Member(
                MemberId.of(entity.memberId),
                com.andre_nathan.gym_webservice.member.domain.model.FullName.of(entity.fullName),
                entity.dateOfBirth,
                com.andre_nathan.gym_webservice.member.domain.model.EmailAddress.of(entity.email),
                com.andre_nathan.gym_webservice.member.domain.model.PhoneNumber.of(entity.phone),
                new com.andre_nathan.gym_webservice.member.domain.model.MembershipPlan(
                        membershipPlanEntity.planId,
                        membershipPlanEntity.planName,
                        membershipPlanEntity.durationInMonths,
                        membershipPlanEntity.price
                ),
                com.andre_nathan.gym_webservice.member.domain.model.MembershipStatus.valueOf(entity.membershipStatus),
                entity.membershipStartDate,
                entity.membershipEndDate
        );
    }
}
