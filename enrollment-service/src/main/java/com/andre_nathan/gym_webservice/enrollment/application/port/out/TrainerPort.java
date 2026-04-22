package com.andre_nathan.gym_webservice.enrollment.application.port.out;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public interface TrainerPort {
    Optional<TrainerSnapshot> findById(String trainerId);

    record TrainerSnapshot(
            String trainerId,
            String fullName,
            String specialty,
            boolean active
    ) {
        public boolean canTeach(String classType) {
            String normalizedClassType = Objects.requireNonNull(classType, "classType cannot be null")
                    .trim()
                    .toLowerCase(Locale.ROOT);
            String normalizedSpecialty = Objects.requireNonNull(specialty, "specialty cannot be null")
                    .trim()
                    .toLowerCase(Locale.ROOT);

            return normalizedSpecialty.equals(normalizedClassType)
                    || normalizedSpecialty.contains(normalizedClassType)
                    || normalizedClassType.contains(normalizedSpecialty);
        }
    }
}
