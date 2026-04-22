package com.andre_nathan.gym_webservice.member.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "members")
public class MemberJpaEntity {
    @Id
    @Column(name = "member_id", nullable = false, updatable = false)
    public String memberId;

    @Column(name = "full_name", nullable = false)
    public String fullName;

    @Column(name = "date_of_birth", nullable = false)
    public LocalDate dateOfBirth;

    @Column(name = "email", nullable = false, unique = true)
    public String email;

    @Column(name = "phone", nullable = false)
    public String phone;

    @Column(name = "membership_plan_id", nullable = false)
    public UUID membershipPlanId;

    @Column(name = "membership_status", nullable = false)
    public String membershipStatus;

    @Column(name = "membership_start_date", nullable = false)
    public LocalDate membershipStartDate;

    @Column(name = "membership_end_date", nullable = false)
    public LocalDate membershipEndDate;

    protected MemberJpaEntity() {
    }
}
