package com.andre_nathan.gym_webservice.member.domain.model;

import com.andre_nathan.gym_webservice.member.domain.exception.ExpiredMembershipCannotBeActiveException;
import com.andre_nathan.gym_webservice.member.domain.exception.InvalidMembershipPeriodException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MemberDomainTest {

    @Test
    void createsValidMember() {
        Member member = new Member(
                MemberId.newId(),
                FullName.of("Taylor Morgan"),
                LocalDate.of(1993, 6, 3),
                EmailAddress.of("taylor.morgan@example.com"),
                PhoneNumber.of("+15145559999"),
                new MembershipPlan(UUID.randomUUID(), "Standard", 12, new BigDecimal("499.99")),
                MembershipStatus.ACTIVE,
                LocalDate.now().minusDays(1),
                LocalDate.now().plusMonths(11)
        );

        assertTrue(member.isMembershipActive());
        assertFalse(member.isMembershipExpired());
    }

    @Test
    void rejectsMembershipPeriodWhenEndIsNotAfterStart() {
        assertThrows(InvalidMembershipPeriodException.class, () -> new Member(
                MemberId.newId(),
                FullName.of("Taylor Morgan"),
                LocalDate.of(1993, 6, 3),
                EmailAddress.of("taylor.morgan@example.com"),
                PhoneNumber.of("+15145559999"),
                new MembershipPlan(UUID.randomUUID(), "Standard", 12, new BigDecimal("499.99")),
                MembershipStatus.ACTIVE,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 1)
        ));
    }

    @Test
    void rejectsActiveStatusForExpiredMembership() {
        assertThrows(ExpiredMembershipCannotBeActiveException.class, () -> new Member(
                MemberId.newId(),
                FullName.of("Taylor Morgan"),
                LocalDate.of(1993, 6, 3),
                EmailAddress.of("taylor.morgan@example.com"),
                PhoneNumber.of("+15145559999"),
                new MembershipPlan(UUID.randomUUID(), "Standard", 12, new BigDecimal("499.99")),
                MembershipStatus.ACTIVE,
                LocalDate.now().minusYears(2),
                LocalDate.now().minusDays(1)
        ));
    }
}
