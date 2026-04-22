package com.andre_nathan.gym_webservice.member.application.exception;

import com.andre_nathan.gym_webservice.member.domain.model.MemberId;

public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException(MemberId memberId) {
        super("Member not found: " + memberId);
    }
}
