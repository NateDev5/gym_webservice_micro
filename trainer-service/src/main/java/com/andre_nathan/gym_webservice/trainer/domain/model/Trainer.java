package com.andre_nathan.gym_webservice.trainer.domain.model;

import com.andre_nathan.gym_webservice.trainer.domain.exception.InvalidTrainerEmailException;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class Trainer {
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final TrainerId trainerId;
    private final FullName fullName;
    private final String email;
    private final Specialty specialty;
    private final boolean active;
    private final List<TrainerCertification> certifications;

    public Trainer(
            TrainerId trainerId,
            FullName fullName,
            String email,
            Specialty specialty,
            boolean active,
            List<TrainerCertification> certifications
    ) {
        this.trainerId = Objects.requireNonNull(trainerId, "trainerId cannot be null");
        this.fullName = Objects.requireNonNull(fullName, "fullName cannot be null");
        this.email = validateEmail(email);
        this.specialty = Objects.requireNonNull(specialty, "specialty cannot be null");
        this.active = active;
        this.certifications = List.copyOf(Objects.requireNonNull(certifications, "certifications cannot be null"));
    }

    public TrainerId getTrainerId() {
        return trainerId;
    }

    public FullName getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public Specialty getSpecialty() {
        return specialty;
    }

    public boolean isActive() {
        return active;
    }

    public List<TrainerCertification> getCertifications() {
        return certifications;
    }

    public boolean canTeach(String classType) {
        return specialty.matches(classType);
    }

    private String validateEmail(String email) {
        String normalizedValue = Objects.requireNonNull(email, "email cannot be null").trim();
        if (!EMAIL_PATTERN.matcher(normalizedValue).matches()) {
            throw new InvalidTrainerEmailException();
        }
        return normalizedValue;
    }
}
