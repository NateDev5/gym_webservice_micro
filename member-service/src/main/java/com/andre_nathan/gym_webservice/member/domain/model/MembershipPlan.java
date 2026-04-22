package com.andre_nathan.gym_webservice.member.domain.model;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class MembershipPlan {
    private final UUID planId;
    private String planName;
    private Integer durationInMonths;
    private BigDecimal price;

    public MembershipPlan(UUID planId, String planName, Integer durationInMonths, BigDecimal price) {
        this.planId = Objects.requireNonNull(planId, "planId cannot be null");
        this.planName = Objects.requireNonNull(planName, "planName cannot be null").trim();
        this.durationInMonths = Objects.requireNonNull(durationInMonths, "durationInMonths cannot be null");
        this.price = Objects.requireNonNull(price, "price cannot be null");

        validate();
    }

    public UUID getPlanId() {
        return planId;
    }

    public String getPlanName() {
        return planName;
    }

    public Integer getDurationInMonths() {
        return durationInMonths;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public boolean isFree() {
        return BigDecimal.ZERO.compareTo(price) == 0;
    }

    private void validate() {
        if (planId.toString().isEmpty()) {
            throw new IllegalArgumentException("planId cannot be blank");
        }

        if (planName.isEmpty()) {
            throw new IllegalArgumentException("planName cannot be blank");
        }

        if (durationInMonths <= 0) {
            throw new IllegalArgumentException("durationInMonths must be greater than zero");
        }

        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("price cannot be negative");
        }
    }
}
