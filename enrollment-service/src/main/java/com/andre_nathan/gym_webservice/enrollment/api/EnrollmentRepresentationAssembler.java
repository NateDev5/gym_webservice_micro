package com.andre_nathan.gym_webservice.enrollment.api;

import com.andre_nathan.gym_webservice.enrollment.api.dto.EnrollmentResponse;
import com.andre_nathan.gym_webservice.enrollment.domain.model.Enrollment;
import com.andre_nathan.gym_webservice.enrollment.domain.model.EnrollmentItem;
import com.andre_nathan.gym_webservice.member.api.MemberController;
import com.andre_nathan.gym_webservice.member.application.port.out.MemberRepositoryPort;
import com.andre_nathan.gym_webservice.schedule.api.ScheduleController;
import com.andre_nathan.gym_webservice.schedule.application.port.out.ScheduleRepositoryPort;
import com.andre_nathan.gym_webservice.trainer.api.TrainerController;
import com.andre_nathan.gym_webservice.trainer.application.port.out.TrainerRepositoryPort;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class EnrollmentRepresentationAssembler extends RepresentationModelAssemblerSupport<Enrollment, EnrollmentResponse> {
    private final MemberRepositoryPort memberRepository;
    private final TrainerRepositoryPort trainerRepository;
    private final ScheduleRepositoryPort scheduleRepository;

    public EnrollmentRepresentationAssembler(
            MemberRepositoryPort memberRepository,
            TrainerRepositoryPort trainerRepository,
            ScheduleRepositoryPort scheduleRepository
    ) {
        super(EnrollmentController.class, EnrollmentResponse.class);
        this.memberRepository = memberRepository;
        this.trainerRepository = trainerRepository;
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public EnrollmentResponse toModel(Enrollment enrollment) {
        var member = memberRepository.findById(enrollment.getMemberId()).orElse(null);
        var items = enrollment.getRegisteredClasses().stream()
                .map(this::toItemResponse)
                .toList();

        EnrollmentResponse response = new EnrollmentResponse(
                enrollment.getEnrollmentId().value(),
                enrollment.getMemberId().value(),
                member == null ? null : member.getFullName().value(),
                member == null ? null : member.getMembershipStatus().name(),
                items
        );

        response.add(linkTo(methodOn(EnrollmentController.class).getById(enrollment.getEnrollmentId().value())).withSelfRel());
        response.add(linkTo(methodOn(EnrollmentController.class).getAll()).withRel("enrollments"));
        response.add(linkTo(methodOn(EnrollmentController.class).getByMemberId(enrollment.getMemberId().value())).withRel("member-enrollments"));

        response.add(linkTo(methodOn(MemberController.class).get(enrollment.getMemberId().value())).withRel("member"));

        if (!enrollment.getRegisteredClasses().isEmpty()) {
            EnrollmentItem lastItem = enrollment.getRegisteredClasses().get(enrollment.getRegisteredClasses().size() - 1);
            response.add(linkTo(methodOn(ScheduleController.class).getByClassSessionId(lastItem.getClassSessionId().value())).withRel("class-session"));
            response.add(linkTo(methodOn(TrainerController.class).getById(lastItem.getTrainerId())).withRel("trainer"));
        }

        return response;
    }

    private EnrollmentResponse.EnrollmentItemResponse toItemResponse(EnrollmentItem item) {
        var schedule = scheduleRepository.findByClassSessionId(item.getClassSessionId().value()).orElse(null);
        var trainer = trainerRepository.findById(com.andre_nathan.gym_webservice.trainer.domain.model.TrainerId.of(item.getTrainerId())).orElse(null);

        return new EnrollmentResponse.EnrollmentItemResponse(
                item.getRegistrationId(),
                item.getEnrollmentDate().value(),
                item.getEnrollmentStatus().name(),
                item.getClassSessionId().value(),
                item.getScheduleId(),
                schedule == null ? null : schedule.getClassName(),
                schedule == null ? null : schedule.getClassType(),
                item.getTrainerId(),
                trainer == null ? null : trainer.getFullName().value(),
                schedule == null ? null : schedule.getClassSession().getSessionStatus(),
                schedule == null ? null : schedule.getTimeSlot().start(),
                schedule == null ? null : schedule.getTimeSlot().end(),
                schedule == null ? null : schedule.getRoom().getRoomId().value(),
                schedule == null ? null : schedule.getRoom().getRoomName(),
                item.getSeatNumber()
        );
    }
}
