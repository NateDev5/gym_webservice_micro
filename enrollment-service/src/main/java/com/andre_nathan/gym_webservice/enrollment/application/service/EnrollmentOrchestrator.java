package com.andre_nathan.gym_webservice.enrollment.application.service;

import com.andre_nathan.gym_webservice.enrollment.application.exception.ClassSessionFullException;
import com.andre_nathan.gym_webservice.enrollment.application.exception.ClassSessionNotFoundException;
import com.andre_nathan.gym_webservice.enrollment.application.exception.EnrollmentNotFoundException;
import com.andre_nathan.gym_webservice.enrollment.application.exception.InactiveTrainerException;
import com.andre_nathan.gym_webservice.enrollment.application.exception.InvalidMembershipException;
import com.andre_nathan.gym_webservice.enrollment.application.exception.MemberNotFoundException;
import com.andre_nathan.gym_webservice.enrollment.application.exception.TrainerNotFoundException;
import com.andre_nathan.gym_webservice.enrollment.application.exception.TrainerSpecialtyMismatchException;
import com.andre_nathan.gym_webservice.enrollment.application.port.out.EnrollmentRepositoryPort;
import com.andre_nathan.gym_webservice.enrollment.application.port.out.MemberPort;
import com.andre_nathan.gym_webservice.enrollment.application.port.out.SchedulePort;
import com.andre_nathan.gym_webservice.enrollment.application.port.out.TrainerPort;
import com.andre_nathan.gym_webservice.enrollment.domain.model.ClassSessionId;
import com.andre_nathan.gym_webservice.enrollment.domain.model.Enrollment;
import com.andre_nathan.gym_webservice.enrollment.domain.model.EnrollmentId;
import com.andre_nathan.gym_webservice.enrollment.domain.model.EnrollmentItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EnrollmentOrchestrator {
    private final EnrollmentRepositoryPort enrollmentRepository;
    private final MemberPort memberPort;
    private final TrainerPort trainerPort;
    private final SchedulePort schedulePort;

    public EnrollmentOrchestrator(
            EnrollmentRepositoryPort enrollmentRepository,
            MemberPort memberPort,
            TrainerPort trainerPort,
            SchedulePort schedulePort
    ) {
        this.enrollmentRepository = enrollmentRepository;
        this.memberPort = memberPort;
        this.trainerPort = trainerPort;
        this.schedulePort = schedulePort;
    }

    @Transactional
    public Enrollment enrollMemberInClass(
            String memberId,
            String classSessionId,
            String trainerId,
            String scheduleId
    ) {
        String parsedMemberId = requireText(memberId, "memberId");
        String parsedClassSessionId = requireText(classSessionId, "classSessionId");
        String parsedTrainerId = requireText(trainerId, "trainerId");
        String parsedScheduleId = requireText(scheduleId, "scheduleId");

        MemberPort.MemberSnapshot member = getMemberById(parsedMemberId);
        validateMembershipIsActive(member);
        SchedulePort.ScheduleSnapshot schedule = getScheduleByClassSessionId(parsedClassSessionId);
        if (!schedule.scheduleId().equals(parsedScheduleId)) {
            throw new IllegalArgumentException("scheduleId does not match classSessionId");
        }
        if (!schedule.trainerId().equals(parsedTrainerId)) {
            throw new IllegalArgumentException("trainerId does not match classSession trainer");
        }

        TrainerPort.TrainerSnapshot trainer = getTrainerById(parsedTrainerId);

        if (!schedule.hasAvailableSeat()) {
            throw new ClassSessionFullException(ClassSessionId.of(parsedClassSessionId));
        }

        validateTrainer(trainer, schedule.classType());

        Enrollment enrollment = getOrCreateEnrollmentForMember(parsedMemberId);
        enrollment.enroll(
                ClassSessionId.of(parsedClassSessionId),
                parsedTrainerId,
                parsedScheduleId,
                schedule.nextSeatNumber()
        );

        schedulePort.save(schedule.incrementEnrollment());

        return enrollmentRepository.save(enrollment);
    }

    @Transactional
    public Enrollment cancelEnrollment(String memberId, String classSessionId) {
        String parsedMemberId = requireText(memberId, "memberId");
        ClassSessionId parsedClassSessionId = ClassSessionId.of(requireText(classSessionId, "classSessionId"));

        getMemberById(parsedMemberId);

        Enrollment enrollment = getEnrollmentForMember(parsedMemberId);
        EnrollmentItem item = enrollment.getRegistrationFor(parsedClassSessionId);
        boolean shouldReleaseSeat = !item.isCancelled();
        enrollment.cancelEnrollment(parsedClassSessionId);

        if (shouldReleaseSeat) {
            SchedulePort.ScheduleSnapshot schedule = schedulePort.findById(item.getScheduleId())
                    .orElseThrow(() -> new ClassSessionNotFoundException(parsedClassSessionId.value()));
            schedulePort.save(schedule.decrementEnrollment());
        }

        return enrollmentRepository.save(enrollment);
    }

    @Transactional(readOnly = true)
    public Enrollment getEnrollmentById(String enrollmentId) {
        EnrollmentId parsedEnrollmentId = EnrollmentId.of(requireText(enrollmentId, "enrollmentId"));
        return enrollmentRepository.findById(parsedEnrollmentId)
                .orElseThrow(() -> new EnrollmentNotFoundException(parsedEnrollmentId));
    }

    @Transactional(readOnly = true)
    public List<Enrollment> getEnrollmentsForMember(String memberId) {
        String parsedMemberId = requireText(memberId, "memberId");
        getMemberById(parsedMemberId);
        return enrollmentRepository.findAllForMember(parsedMemberId);
    }

    @Transactional(readOnly = true)
    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    private MemberPort.MemberSnapshot getMemberById(String memberId) {
        return memberPort.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
    }

    private TrainerPort.TrainerSnapshot getTrainerById(String trainerId) {
        return trainerPort.findById(trainerId)
                .orElseThrow(() -> new TrainerNotFoundException(trainerId));
    }

    private SchedulePort.ScheduleSnapshot getScheduleByClassSessionId(String classSessionId) {
        return schedulePort.findByClassSessionId(classSessionId)
                .orElseThrow(() -> new ClassSessionNotFoundException(classSessionId));
    }

    private void validateMembershipIsActive(MemberPort.MemberSnapshot member) {
        if (!member.isMembershipActive() || member.isMembershipExpired()) {
            throw new InvalidMembershipException(member.memberId());
        }
    }

    private void validateTrainer(TrainerPort.TrainerSnapshot trainer, String classType) {
        if (!trainer.active()) {
            throw new InactiveTrainerException(trainer.trainerId());
        }

        if (!trainer.canTeach(classType)) {
            throw new TrainerSpecialtyMismatchException(trainer.specialty(), classType);
        }
    }

    private Enrollment getOrCreateEnrollmentForMember(String memberId) {
        List<Enrollment> enrollments = enrollmentRepository.findAllForMember(memberId);
        if (enrollments.isEmpty()) {
            Enrollment newEnrollment = new Enrollment(
                    EnrollmentId.newId(),
                    memberId,
                    List.of()
            );
            return enrollmentRepository.save(newEnrollment);
        }
        return enrollments.get(0);
    }

    private Enrollment getEnrollmentForMember(String memberId) {
        List<Enrollment> enrollments = enrollmentRepository.findAllForMember(memberId);
        if (enrollments.isEmpty()) {
            throw new EnrollmentNotFoundException(EnrollmentId.of(memberId));
        }
        return enrollments.get(0);
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }

        return value.trim();
    }
}
