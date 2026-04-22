package com.andre_nathan.gym_webservice.enrollment.api;

import com.andre_nathan.gym_webservice.enrollment.api.dto.EnrollmentResponse;
import com.andre_nathan.gym_webservice.enrollment.application.port.out.MemberPort;
import com.andre_nathan.gym_webservice.enrollment.application.port.out.SchedulePort;
import com.andre_nathan.gym_webservice.enrollment.application.port.out.TrainerPort;
import com.andre_nathan.gym_webservice.enrollment.domain.model.Enrollment;
import com.andre_nathan.gym_webservice.enrollment.domain.model.EnrollmentItem;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class EnrollmentRepresentationAssembler extends RepresentationModelAssemblerSupport<Enrollment, EnrollmentResponse> {
    private final MemberPort memberPort;
    private final TrainerPort trainerPort;
    private final SchedulePort schedulePort;

    public EnrollmentRepresentationAssembler(
            MemberPort memberPort,
            TrainerPort trainerPort,
            SchedulePort schedulePort
    ) {
        super(EnrollmentController.class, EnrollmentResponse.class);
        this.memberPort = memberPort;
        this.trainerPort = trainerPort;
        this.schedulePort = schedulePort;
    }

    @Override
    public EnrollmentResponse toModel(Enrollment enrollment) {
        var member = memberPort.findById(enrollment.getMemberId()).orElse(null);
        var items = enrollment.getRegisteredClasses().stream()
                .map(this::toItemResponse)
                .toList();

        EnrollmentResponse response = new EnrollmentResponse(
                enrollment.getEnrollmentId().value(),
                enrollment.getMemberId(),
                member == null ? null : member.fullName(),
                member == null ? null : member.membershipStatus(),
                items
        );

        response.add(linkTo(methodOn(EnrollmentController.class).getById(enrollment.getEnrollmentId().value())).withSelfRel());
        response.add(linkTo(methodOn(EnrollmentController.class).getAll()).withRel("enrollments"));
        response.add(linkTo(methodOn(EnrollmentController.class).getByMemberId(enrollment.getMemberId())).withRel("member-enrollments"));

        response.add(Link.of("/api/members/" + enrollment.getMemberId()).withRel("member"));

        if (!enrollment.getRegisteredClasses().isEmpty()) {
            EnrollmentItem lastItem = enrollment.getRegisteredClasses().get(enrollment.getRegisteredClasses().size() - 1);
            response.add(Link.of("/api/schedules/class-sessions/" + lastItem.getClassSessionId().value()).withRel("class-session"));
            response.add(Link.of("/api/trainers/" + lastItem.getTrainerId()).withRel("trainer"));
        }

        return response;
    }

    private EnrollmentResponse.EnrollmentItemResponse toItemResponse(EnrollmentItem item) {
        var schedule = schedulePort.findByClassSessionId(item.getClassSessionId().value()).orElse(null);
        var trainer = trainerPort.findById(item.getTrainerId()).orElse(null);

        return new EnrollmentResponse.EnrollmentItemResponse(
                item.getRegistrationId(),
                item.getEnrollmentDate().value(),
                item.getEnrollmentStatus().name(),
                item.getClassSessionId().value(),
                item.getScheduleId(),
                schedule == null ? null : schedule.className(),
                schedule == null ? null : schedule.classType(),
                item.getTrainerId(),
                trainer == null ? null : trainer.fullName(),
                schedule == null ? null : schedule.sessionStatus(),
                schedule == null ? null : schedule.startTime(),
                schedule == null ? null : schedule.endTime(),
                schedule == null ? null : schedule.roomId(),
                schedule == null ? null : schedule.roomName(),
                item.getSeatNumber()
        );
    }
}
