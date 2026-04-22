package com.andre_nathan.gym_webservice.trainer.domain.model;

import com.andre_nathan.gym_webservice.trainer.domain.exception.InvalidCertificationPeriodException;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class TrainerCertification {
    private final UUID certificationId;
    private final String certificateName;
    private final LocalDate issuedDate;
    private final LocalDate expiryDate;

    public TrainerCertification(
            UUID certificationId,
            String certificateName,
            LocalDate issuedDate,
            LocalDate expiryDate
    ) {
        this.certificationId = Objects.requireNonNull(certificationId, "certificationId cannot be null");
        this.certificateName = Objects.requireNonNull(certificateName, "certificateName cannot be null").trim();
        this.issuedDate = Objects.requireNonNull(issuedDate, "issuedDate cannot be null");
        this.expiryDate = Objects.requireNonNull(expiryDate, "expiryDate cannot be null");

        if (!this.expiryDate.isAfter(this.issuedDate)) {
            throw new InvalidCertificationPeriodException();
        }
    }

    public UUID getCertificationId() {
        return certificationId;
    }

    public String getCertificateName() {
        return certificateName;
    }

    public LocalDate getIssuedDate() {
        return issuedDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }
}
