package com.andre_nathan.gym_webservice.member.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record MemberResponse(
        String memberId,
        String fullName,
        LocalDate dateOfBirth,
        String email,
        String phone,
        UUID membershipPlanId,
        String membershipPlanName,
        Integer membershipDurationInMonths,
        BigDecimal membershipPrice,
        String membershipStatus,
        LocalDate membershipStartDate,
        LocalDate membershipEndDate
) {
}
