package com.andre_nathan.gym_webservice.enrollment.application.port.out;

import java.time.LocalDate;
import java.util.Optional;

public interface MemberPort {
    Optional<MemberSnapshot> findById(String memberId);

    record MemberSnapshot(
            String memberId,
            String fullName,
            String membershipStatus,
            LocalDate membershipEndDate
    ) {
        public boolean isMembershipActive() {
            return membershipStatus != null && "ACTIVE".equalsIgnoreCase(membershipStatus);
        }

        public boolean isMembershipExpired() {
            return membershipEndDate != null && membershipEndDate.isBefore(LocalDate.now());
        }
    }
}
