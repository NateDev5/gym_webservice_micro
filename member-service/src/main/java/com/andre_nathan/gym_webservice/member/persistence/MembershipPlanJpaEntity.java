package com.andre_nathan.gym_webservice.member.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "membership_plans")
public class MembershipPlanJpaEntity {
    @Id
    @Column(name = "plan_id", nullable = false, updatable = false)
    public UUID planId;

    @Column(name = "plan_name", nullable = false)
    public String planName;

    @Column(name = "duration_in_months", nullable = false)
    public Integer durationInMonths;

    @Column(name = "price", nullable = false)
    public BigDecimal price;

    protected MembershipPlanJpaEntity() {
    }
}
