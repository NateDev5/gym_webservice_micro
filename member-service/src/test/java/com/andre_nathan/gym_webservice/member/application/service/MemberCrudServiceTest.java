package com.andre_nathan.gym_webservice.member.application.service;

import com.andre_nathan.gym_webservice.member.application.exception.DuplicateMemberException;
import com.andre_nathan.gym_webservice.member.application.exception.MemberNotFoundException;
import com.andre_nathan.gym_webservice.member.application.exception.MembershipPlanNotFoundException;
import com.andre_nathan.gym_webservice.member.application.port.out.MemberRepositoryPort;
import com.andre_nathan.gym_webservice.member.application.port.out.MembershipPlanRepositoryPort;
import com.andre_nathan.gym_webservice.member.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberCrudServiceTest {

    @Mock
    private MemberRepositoryPort repo;

    @Mock
    private MembershipPlanRepositoryPort membershipPlanRepository;

    @InjectMocks
    private MemberCrudService service;

    private MembershipPlan membershipPlan;

    @BeforeEach
    void setUp() {
        membershipPlan = new MembershipPlan(
                UUID.randomUUID(),
                "Standard",
                12,
                new BigDecimal("499.99")
        );
    }

    @Test
    void createSuccess() {
        when(repo.existsByEmail("taylor@example.com")).thenReturn(false);
        when(membershipPlanRepository.findById(membershipPlan.getPlanId())).thenReturn(Optional.of(membershipPlan));
        when(repo.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Member created = service.create(
                "Taylor Morgan",
                LocalDate.of(1993, 6, 3),
                "taylor@example.com",
                "+15145550000",
                membershipPlan.getPlanId(),
                "ACTIVE",
                LocalDate.now(),
                LocalDate.now().plusMonths(6)
        );

        assertEquals("taylor@example.com", created.getEmailAddress().value());
        verify(repo).save(any(Member.class));
    }

    @Test
    void createDuplicateEmailThrowsConflict() {
        when(repo.existsByEmail("taylor@example.com")).thenReturn(true);

        assertThrows(DuplicateMemberException.class, () -> service.create(
                "Taylor Morgan",
                LocalDate.of(1993, 6, 3),
                "taylor@example.com",
                "+15145550000",
                membershipPlan.getPlanId(),
                "ACTIVE",
                LocalDate.now(),
                LocalDate.now().plusMonths(6)
        ));
        verify(repo, never()).save(any(Member.class));
    }

    @Test
    void createMissingMembershipPlanThrowsNotFound() {
        when(repo.existsByEmail(anyString())).thenReturn(false);
        when(membershipPlanRepository.findById(membershipPlan.getPlanId())).thenReturn(Optional.empty());

        assertThrows(MembershipPlanNotFoundException.class, () -> service.create(
                "Taylor Morgan",
                LocalDate.of(1993, 6, 3),
                "taylor@example.com",
                "+15145550000",
                membershipPlan.getPlanId(),
                "ACTIVE",
                LocalDate.now(),
                LocalDate.now().plusMonths(6)
        ));
        verify(repo, never()).save(any(Member.class));
    }

    @Test
    void getByIdSuccess() {
        Member member = member("member-1", "m1@example.com");
        when(repo.findById(MemberId.of("member-1"))).thenReturn(Optional.of(member));

        Member found = service.getById("member-1");

        assertEquals("member-1", found.getMemberId().value());
    }

    @Test
    void getByIdMissingThrowsNotFound() {
        when(repo.findById(MemberId.of("missing"))).thenReturn(Optional.empty());
        assertThrows(MemberNotFoundException.class, () -> service.getById("missing"));
    }

    @Test
    void getAllReturnsMembers() {
        when(repo.findAll()).thenReturn(List.of(member("member-1", "m1@example.com")));

        List<Member> members = service.getAll();

        assertEquals(1, members.size());
    }

    @Test
    void updateMissingResourceThrowsNotFound() {
        when(repo.findById(MemberId.of("missing"))).thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class, () -> service.update(
                "missing",
                "Taylor Morgan",
                LocalDate.of(1993, 6, 3),
                "taylor@example.com",
                "+15145550000",
                membershipPlan.getPlanId(),
                "ACTIVE",
                LocalDate.now(),
                LocalDate.now().plusMonths(6)
        ));
        verify(repo, never()).save(any(Member.class));
    }

    @Test
    void deleteSuccess() {
        Member existing = member("member-1", "m1@example.com");
        when(repo.findById(MemberId.of("member-1"))).thenReturn(Optional.of(existing));

        service.delete("member-1");

        verify(repo).deleteById(MemberId.of("member-1"));
    }

    private Member member(String memberId, String email) {
        return new Member(
                MemberId.of(memberId),
                FullName.of("Test Member"),
                LocalDate.of(1992, 1, 1),
                EmailAddress.of(email),
                PhoneNumber.of("+15145550123"),
                membershipPlan,
                MembershipStatus.ACTIVE,
                LocalDate.now(),
                LocalDate.now().plusMonths(6)
        );
    }
}
