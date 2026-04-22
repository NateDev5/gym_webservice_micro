package com.andre_nathan.gym_webservice.member.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record CreateMemberRequest(
        @NotBlank String fullName,
        @NotNull LocalDate dateOfBirth,
        @NotBlank @Email String email,
        @NotBlank String phone,
        @NotNull UUID membershipPlanId,
        @NotBlank String membershipStatus,
        @NotNull LocalDate membershipStartDate,
        @NotNull LocalDate membershipEndDate
) {
}
