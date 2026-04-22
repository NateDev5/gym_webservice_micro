package com.andre_nathan.gym_webservice.enrollment.application.service;

import com.andre_nathan.gym_webservice.enrollment.application.exception.EnrollmentNotFoundException;
import com.andre_nathan.gym_webservice.enrollment.application.exception.InvalidMembershipException;
import com.andre_nathan.gym_webservice.enrollment.application.port.out.EnrollmentRepositoryPort;
import com.andre_nathan.gym_webservice.enrollment.domain.model.ClassSessionId;
import com.andre_nathan.gym_webservice.enrollment.domain.model.Enrollment;
import com.andre_nathan.gym_webservice.enrollment.domain.model.EnrollmentId;
import com.andre_nathan.gym_webservice.enrollment.domain.model.EnrollmentItem;
import com.andre_nathan.gym_webservice.member.application.exception.MemberNotFoundException;
import com.andre_nathan.gym_webservice.member.application.port.out.MemberRepositoryPort;
import com.andre_nathan.gym_webservice.member.domain.model.Member;
import com.andre_nathan.gym_webservice.member.domain.model.MemberId;
import com.andre_nathan.gym_webservice.schedule.application.exception.ClassSessionNotFoundException;
import com.andre_nathan.gym_webservice.schedule.application.port.out.ScheduleRepositoryPort;
import com.andre_nathan.gym_webservice.schedule.domain.model.Schedule;
import com.andre_nathan.gym_webservice.schedule.domain.model.ScheduleId;
import com.andre_nathan.gym_webservice.trainer.application.exception.InactiveTrainerException;
import com.andre_nathan.gym_webservice.trainer.application.exception.TrainerNotFoundException;
import com.andre_nathan.gym_webservice.trainer.application.exception.TrainerSpecialtyMismatchException;
import com.andre_nathan.gym_webservice.trainer.application.port.out.TrainerRepositoryPort;
import com.andre_nathan.gym_webservice.trainer.domain.model.Trainer;
import com.andre_nathan.gym_webservice.trainer.domain.model.TrainerId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EnrollmentOrchestrator {
    private final EnrollmentRepositoryPort enrollmentRepository;
    private final MemberRepositoryPort memberRepository;
    private final TrainerRepositoryPort trainerRepository;
    private final ScheduleRepositoryPort scheduleRepository;

    public EnrollmentOrchestrator(
            EnrollmentRepositoryPort enrollmentRepository,
            MemberRepositoryPort memberRepository,
            TrainerRepositoryPort trainerRepository,
            ScheduleRepositoryPort scheduleRepository
    ) {
        this.enrollmentRepository = enrollmentRepository;
        this.memberRepository = memberRepository;
        this.trainerRepository = trainerRepository;
        this.scheduleRepository = scheduleRepository;
    }

    @Transactional
    public Enrollment enrollMemberInClass(String memberId, String classSessionId) {
        MemberId parsedMemberId = MemberId.of(requireText(memberId, "memberId"));
        ClassSessionId parsedClassSessionId = ClassSessionId.of(requireText(classSessionId, "classSessionId"));

        Member member = getMemberById(parsedMemberId);
        validateMembershipIsActive(member);
        Schedule schedule = getScheduleByClassSessionId(parsedClassSessionId.value());
        Trainer trainer = getTrainerById(schedule.getTrainerId());

        if (!schedule.hasAvailableSeat()) {
            throw new com.andre_nathan.gym_webservice.enrollment.application.exception.ClassSessionFullException(parsedClassSessionId);
        }

        validateTrainer(trainer, schedule.getClassType());

        Enrollment enrollment = getOrCreateEnrollmentForMember(parsedMemberId);
        enrollment.enroll(
                parsedClassSessionId,
                trainer.getTrainerId().value(),
                schedule.getScheduleId().value(),
                schedule.nextSeatNumber()
        );

        schedule.incrementEnrollment();
        scheduleRepository.save(schedule);

        return enrollmentRepository.save(enrollment);
    }

    @Transactional
    public Enrollment cancelEnrollment(String memberId, String classSessionId) {
        MemberId parsedMemberId = MemberId.of(requireText(memberId, "memberId"));
        ClassSessionId parsedClassSessionId = ClassSessionId.of(requireText(classSessionId, "classSessionId"));

        getMemberById(parsedMemberId);

        Enrollment enrollment = getEnrollmentForMember(parsedMemberId);
        EnrollmentItem item = enrollment.getRegistrationFor(parsedClassSessionId);
        boolean shouldReleaseSeat = !item.isCancelled();
        enrollment.cancelEnrollment(parsedClassSessionId);

        if (shouldReleaseSeat) {
            Schedule schedule = scheduleRepository.findById(ScheduleId.of(item.getScheduleId()))
                    .orElseThrow(() -> new ClassSessionNotFoundException(parsedClassSessionId.value()));
            schedule.decrementEnrollment();
            scheduleRepository.save(schedule);
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
        MemberId parsedMemberId = MemberId.of(requireText(memberId, "memberId"));
        getMemberById(parsedMemberId);
        return enrollmentRepository.findAllForMember(parsedMemberId);
    }

    @Transactional(readOnly = true)
    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    private Member getMemberById(MemberId memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
    }

    private Trainer getTrainerById(String trainerId) {
        TrainerId parsedTrainerId = TrainerId.of(requireText(trainerId, "trainerId"));
        return trainerRepository.findById(parsedTrainerId)
                .orElseThrow(() -> new TrainerNotFoundException(parsedTrainerId));
    }

    private Schedule getScheduleByClassSessionId(String classSessionId) {
        return scheduleRepository.findByClassSessionId(classSessionId)
                .orElseThrow(() -> new ClassSessionNotFoundException(classSessionId));
    }

    private void validateMembershipIsActive(Member member) {
        if (!member.isMembershipActive() || member.isMembershipExpired()) {
            throw new InvalidMembershipException(member.getMemberId());
        }
    }

    private void validateTrainer(Trainer trainer, String classType) {
        if (!trainer.isActive()) {
            throw new InactiveTrainerException(trainer.getTrainerId());
        }

        if (!trainer.canTeach(classType)) {
            throw new TrainerSpecialtyMismatchException(trainer.getSpecialty().value(), classType);
        }
    }

    private Enrollment getOrCreateEnrollmentForMember(MemberId memberId) {
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

    private Enrollment getEnrollmentForMember(MemberId memberId) {
        List<Enrollment> enrollments = enrollmentRepository.findAllForMember(memberId);
        if (enrollments.isEmpty()) {
            throw new EnrollmentNotFoundException(EnrollmentId.of(memberId.value()));
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
