package com.andre_nathan.gym_webservice.member.application.port.out;

import com.andre_nathan.gym_webservice.member.domain.model.Member;
import com.andre_nathan.gym_webservice.member.domain.model.MemberId;

import java.util.List;
import java.util.Optional;

public interface MemberRepositoryPort {
    Member save(Member member);
    Optional<Member> findById(MemberId id);
    boolean existsByEmail(String email);
    boolean existsByEmailExcludingId(String email, MemberId memberId);
    List<Member> findAll();
    void deleteById(MemberId id);
}
