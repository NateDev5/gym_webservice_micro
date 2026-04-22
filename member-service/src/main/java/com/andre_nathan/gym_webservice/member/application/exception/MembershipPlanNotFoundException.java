package com.andre_nathan.gym_webservice.member.application.exception;

import java.util.UUID;

public class MembershipPlanNotFoundException extends RuntimeException {
    public MembershipPlanNotFoundException(UUID planId) {
        super("Membership plan not found: " + planId);
    }
}
