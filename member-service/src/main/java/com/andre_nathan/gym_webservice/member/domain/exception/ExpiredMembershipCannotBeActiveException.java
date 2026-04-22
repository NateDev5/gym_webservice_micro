package com.andre_nathan.gym_webservice.member.domain.exception;

public class ExpiredMembershipCannotBeActiveException extends RuntimeException {
    public ExpiredMembershipCannotBeActiveException() {
        super("Expired memberships cannot be active");
    }
}
