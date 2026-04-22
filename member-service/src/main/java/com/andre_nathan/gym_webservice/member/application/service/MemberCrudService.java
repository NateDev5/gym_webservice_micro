package com.andre_nathan.gym_webservice.member.application.service;

import com.andre_nathan.gym_webservice.member.application.exception.DuplicateMemberException;
import com.andre_nathan.gym_webservice.member.application.exception.MemberNotFoundException;
import com.andre_nathan.gym_webservice.member.application.exception.MembershipPlanNotFoundException;
import com.andre_nathan.gym_webservice.member.application.port.out.MemberRepositoryPort;
import com.andre_nathan.gym_webservice.member.application.port.out.MembershipPlanRepositoryPort;
import com.andre_nathan.gym_webservice.member.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class MemberCrudService {
    private final MemberRepositoryPort repo;
    private final MembershipPlanRepositoryPort membershipPlanRepository;

    public MemberCrudService(MemberRepositoryPort repo, MembershipPlanRepositoryPort membershipPlanRepository) {
        this.repo = repo;
        this.membershipPlanRepository = membershipPlanRepository;
    }

    @Transactional
    public Member create(
            String fullName,
            LocalDate dateOfBirth,
            String emailAddress,
            String phoneNumber,
            UUID membershipPlanId,
            String membershipStatus,
            LocalDate membershipStartDate,
            LocalDate membershipEndDate
    ) {
        if(repo.existsByEmail(emailAddress))
            throw new DuplicateMemberException(emailAddress);

        MembershipPlan membershipPlan = getMembershipPlanById(requireNonNull(membershipPlanId, "membershipPlanId"));

        Member member = new Member(
                MemberId.newId(),
                FullName.of(requireText(fullName, "fullName")),
                dateOfBirth,
                EmailAddress.of(requireText(emailAddress, "emailAddress")),
                PhoneNumber.of(requireText(phoneNumber, "phoneNumber")),
                membershipPlan,
                parseMembershipStatus(membershipStatus),
                membershipStartDate,
                membershipEndDate
        );

        return repo.save(member);
    }

    @Transactional
    public Member update(
            String memberId,
            String fullName,
            LocalDate dateOfBirth,
            String emailAddress,
            String phoneNumber,
            UUID membershipPlanId,
            String membershipStatus,
            LocalDate membershipStartDate,
            LocalDate membershipEndDate
    ) {
        MemberId parsedMemberId = MemberId.of(requireText(memberId, "memberId"));
        getById(parsedMemberId);
        MembershipPlan membershipPlan = getMembershipPlanById(requireNonNull(membershipPlanId, "membershipPlanId"));

        if (repo.existsByEmailExcludingId(emailAddress, parsedMemberId)) {
            throw new DuplicateMemberException(emailAddress);
        }

        Member updatedMember = new Member(
                parsedMemberId,
                FullName.of(requireText(fullName, "fullName")),
                dateOfBirth,
                EmailAddress.of(requireText(emailAddress, "emailAddress")),
                PhoneNumber.of(requireText(phoneNumber, "phoneNumber")),
                membershipPlan,
                parseMembershipStatus(membershipStatus),
                membershipStartDate,
                membershipEndDate
        );

        return repo.save(updatedMember);
    }

    @Transactional(readOnly = true)
    public Member getById(String memberId) {
        MemberId parsedMemberId = MemberId.of(requireText(memberId, "memberId"));
        return getById(parsedMemberId);
    }

    @Transactional(readOnly = true)
    public List<Member> getAll() {
        return repo.findAll();
    }

    @Transactional
    public void delete(String memberId) {
        MemberId parsedMemberId = MemberId.of(requireText(memberId, "memberId"));
        getById(parsedMemberId);
        repo.deleteById(parsedMemberId);
    }

    private Member getById(MemberId memberId) {
        return repo.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
    }

    private MembershipPlan getMembershipPlanById(UUID membershipPlanId) {
        return membershipPlanRepository.findById(membershipPlanId)
                .orElseThrow(() -> new MembershipPlanNotFoundException(membershipPlanId));
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }

        return value.trim();
    }

    private <T> T requireNonNull(T value, String fieldName) {
        return Objects.requireNonNull(value, fieldName + " cannot be null");
    }

    private MembershipStatus parseMembershipStatus(String membershipStatus) {
        try {
            return MembershipStatus.valueOf(requireText(membershipStatus, "membershipStatus").toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid membershipStatus: " + membershipStatus, ex);
        }
    }
}
