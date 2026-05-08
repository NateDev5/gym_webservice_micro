package com.andre_nathan.gym_webservice.member.domain.model;

import com.andre_nathan.gym_webservice.member.domain.exception.InvalidEmailAddressException;
import com.andre_nathan.gym_webservice.member.domain.exception.InvalidFullNameException;
import com.andre_nathan.gym_webservice.member.domain.exception.InvalidPhoneNumberException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MemberValueObjectsTest {

    @Test
    void normalizesAndAcceptsValidValues() {
        FullName fullName = FullName.of("  Jordan   Blake ");
        EmailAddress emailAddress = EmailAddress.of("jordan.blake@example.com");
        PhoneNumber phoneNumber = PhoneNumber.of("+1 514 555 1212");

        assertEquals("Jordan Blake", fullName.value());
        assertEquals("jordan.blake@example.com", emailAddress.value());
        assertEquals("+15145551212", phoneNumber.value());
    }

    @Test
    void rejectsInvalidValues() {
        assertThrows(InvalidFullNameException.class, () -> FullName.of("a"));
        assertThrows(InvalidEmailAddressException.class, () -> EmailAddress.of("not-an-email"));
        assertThrows(InvalidPhoneNumberException.class, () -> PhoneNumber.of("123"));
    }

    @Test
    void membershipPlanValidatesNumericConstraints() {
        assertThrows(IllegalArgumentException.class, () ->
                new MembershipPlan(UUID.randomUUID(), "Plan", 0, BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class, () ->
                new MembershipPlan(UUID.randomUUID(), "Plan", 12, new BigDecimal("-1")));
    }
}
