package com.andre_nathan.gym_webservice.member.api.mapper;

import com.andre_nathan.gym_webservice.member.api.dto.MemberResponse;
import com.andre_nathan.gym_webservice.member.domain.model.Member;

public class MemberApiMapper {
    public static MemberResponse toResponse(Member member) {
        return new MemberResponse(
                member.getMemberId().value(),
                member.getFullName().value(),
                member.getDateOfBirth(),
                member.getEmailAddress().value(),
                member.getPhoneNumber().value(),
                member.getMembershipPlan().getPlanId(),
                member.getMembershipPlan().getPlanName(),
                member.getMembershipPlan().getDurationInMonths(),
                member.getMembershipPlan().getPrice(),
                member.getMembershipStatus().name(),
                member.getMembershipStartDate(),
                member.getMembershipEndDate()
        );
    }
}
