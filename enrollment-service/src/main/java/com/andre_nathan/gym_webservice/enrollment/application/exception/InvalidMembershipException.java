package com.andre_nathan.gym_webservice.enrollment.application.exception;

import com.andre_nathan.gym_webservice.member.domain.model.MemberId;

public class InvalidMembershipException extends RuntimeException {
    public InvalidMembershipException(MemberId memberId) {
        super("Member does not have a valid membership: " + memberId);
    }
}
