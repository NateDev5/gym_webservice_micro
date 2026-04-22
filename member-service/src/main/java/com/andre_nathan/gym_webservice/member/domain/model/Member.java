package com.andre_nathan.gym_webservice.member.domain.model;

import com.andre_nathan.gym_webservice.member.domain.exception.ExpiredMembershipCannotBeActiveException;
import com.andre_nathan.gym_webservice.member.domain.exception.InvalidMembershipPeriodException;

import java.time.LocalDate;
import java.util.Objects;

public class Member {
    private final MemberId memberId;
    private FullName fullName;
    private LocalDate dateOfBirth;
    private EmailAddress emailAddress;
    private PhoneNumber phoneNumber;

    private MembershipPlan membershipPlan;
    private MembershipStatus membershipStatus;
    private LocalDate membershipStartDate;
    private LocalDate membershipEndDate;

    public Member(
            MemberId memberId,
            FullName fullName,
            LocalDate dateOfBirth,
            EmailAddress emailAddress,
            PhoneNumber phoneNumber,

            MembershipPlan membershipPlan,
            MembershipStatus membershipStatus,
            LocalDate membershipStartDate,
            LocalDate membershipEndDate
    ) {
        this.memberId = Objects.requireNonNull(memberId, "memberId cannot be null");
        this.fullName = Objects.requireNonNull(fullName, "fullName cannot be null");
        this.dateOfBirth = Objects.requireNonNull(dateOfBirth, "dateOfBirth cannot be null");
        this.emailAddress = Objects.requireNonNull(emailAddress, "emailAddress cannot be null");
        this.phoneNumber = Objects.requireNonNull(phoneNumber, "phoneNumber cannot be null");

        this.membershipPlan = Objects.requireNonNull(membershipPlan, "membershipPlan cannot be null");
        this.membershipStatus = Objects.requireNonNull(membershipStatus, "membershipStatus cannot be null");
        this.membershipStartDate = Objects.requireNonNull(membershipStartDate, "memberShipStartDate cannot be null");
        this.membershipEndDate = Objects.requireNonNull(membershipEndDate, "memberShipEndDate cannot be null");

        validateMembershipPeriod();
        validateMembershipStatusForDates();
    }

    public MemberId getMemberId() {
        return memberId;
    }

    public FullName getFullName() {
        return fullName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public EmailAddress getEmailAddress() {
        return emailAddress;
    }

    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    public MembershipPlan getMembershipPlan() {
        return membershipPlan;
    }

    public MembershipStatus getMembershipStatus() {
        return membershipStatus;
    }

    public LocalDate getMembershipStartDate() {
        return membershipStartDate;
    }

    public LocalDate getMembershipEndDate() {
        return membershipEndDate;
    }

    public boolean isMembershipActive () {
        return membershipStatus == MembershipStatus.ACTIVE;
    }

    public boolean isMembershipExpired() {
        return membershipEndDate.isBefore(LocalDate.now());
    }

    private void validateMembershipPeriod() {
        if (!membershipEndDate.isAfter(membershipStartDate)) {
            throw new InvalidMembershipPeriodException();
        }
    }

    private void validateMembershipStatusForDates() {
        if (membershipStatus == MembershipStatus.ACTIVE && isMembershipExpired()) {
            throw new ExpiredMembershipCannotBeActiveException();
        }
    }
}
