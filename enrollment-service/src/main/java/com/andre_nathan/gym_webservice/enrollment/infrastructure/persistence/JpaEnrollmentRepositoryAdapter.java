package com.andre_nathan.gym_webservice.enrollment.infrastructure.persistence;

import com.andre_nathan.gym_webservice.enrollment.application.port.out.EnrollmentRepositoryPort;
import com.andre_nathan.gym_webservice.enrollment.domain.model.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class JpaEnrollmentRepositoryAdapter implements EnrollmentRepositoryPort {
    private final SpringDataEnrollmentRepository jpa;

    public JpaEnrollmentRepositoryAdapter(SpringDataEnrollmentRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Enrollment save(Enrollment enrollment) {
        jpa.save(toEntity(enrollment));
        return enrollment;
    }

    @Override
    public Optional<Enrollment> findById(EnrollmentId id) {
        return jpa.findById(id.value()).map(this::toDomain);
    }

    @Override
    public boolean existsById(EnrollmentId id) {
        return jpa.existsById(id.value());
    }

    @Override
    public List<Enrollment> findAll() {
        return jpa.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public List<Enrollment> findAllForMember(String memberId) {
        return jpa.findAllByMemberId(memberId).stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteById(EnrollmentId id) {
        jpa.deleteById(id.value());
    }

    private EnrollmentJpaEntity toEntity(Enrollment enrollment) {
        var e = new EnrollmentJpaEntity();
        e.enrollmentId = enrollment.getEnrollmentId().value();
        e.memberId = enrollment.getMemberId();
        e.registeredClasses.clear();

        for (EnrollmentItem item : enrollment.getRegisteredClasses()) {
            var itemEntity = new EnrollmentItemJpaEntity();
            itemEntity.registrationId = item.getRegistrationId();
            itemEntity.enrollmentDate = item.getEnrollmentDate().value();
            itemEntity.enrollmentStatus = item.getEnrollmentStatus().name();
            itemEntity.classSessionId = item.getClassSessionId().value();
            itemEntity.seatNumber = item.getSeatNumber();
            itemEntity.trainerId = item.getTrainerId();
            itemEntity.scheduleId = item.getScheduleId();
            itemEntity.enrollment = e;
            e.registeredClasses.add(itemEntity);
        }

        return e;
    }

    private Enrollment toDomain(EnrollmentJpaEntity entity) {
        List<EnrollmentItem> items = entity.registeredClasses.stream()
                .map(this::toItemDomain)
                .toList();

        return new Enrollment(
                EnrollmentId.of(entity.enrollmentId),
                entity.memberId,
                items
        );
    }

    private EnrollmentItem toItemDomain(EnrollmentItemJpaEntity entity) {
        return new EnrollmentItem(
                entity.registrationId,
                EnrollmentDate.of(entity.enrollmentDate),
                EnrollmentStatus.valueOf(entity.enrollmentStatus),
                ClassSessionId.of(entity.classSessionId),
                entity.trainerId,
                entity.scheduleId,
                entity.seatNumber
        );
    }
}
