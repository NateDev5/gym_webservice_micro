package com.andre_nathan.gym_webservice.member.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataMemberRepository extends JpaRepository<MemberJpaEntity, String> {
    boolean existsByEmail(String email);
    boolean existsByEmailAndMemberIdNot(String email, String memberId);
}
