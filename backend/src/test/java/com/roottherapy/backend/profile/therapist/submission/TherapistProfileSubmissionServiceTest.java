package com.roottherapy.backend.profile.therapist.submission;

import com.roottherapy.backend.notification.ModerationCommunicationService;
import com.roottherapy.backend.profile.therapist.TherapistProfile;
import com.roottherapy.backend.profile.therapist.TherapistProfileRepository;
import com.roottherapy.backend.profile.therapist.submission.dto.*;
import com.roottherapy.backend.users.AccountStatus;
import com.roottherapy.backend.users.User;
import com.roottherapy.backend.users.UserRepository;
import com.roottherapy.backend.users.UserRole;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TherapistProfileSubmissionServiceTest {

    @Mock
    private TherapistProfileSubmissionRepository submissionRepository;

    @Mock
    private TherapistProfileRepository profileRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModerationCommunicationService communicationService;

    private TherapistProfileSubmissionService submissionService;

    @BeforeEach
    void setUp() {
        submissionService = new TherapistProfileSubmissionService(
                submissionRepository,
                profileRepository,
                userRepository,
                communicationService
        );
    }

    // JUNIT-PROFILE-001
    @Test
    void getOrCreateOwnDraft_noExistingDraft_createsDraftFromLiveProfile() {
        UUID therapistId = UUID.randomUUID();
        UUID submissionId = UUID.randomUUID();

        User therapist = createUser(
                therapistId,
                "therapist@example.com",
                "Jane",
                "Smith",
                UserRole.THERAPIST
        );

        TherapistProfile liveProfile = createProfile(therapist, "REG-12345");
        liveProfile.setQualifications("MSc Counselling and Psychotherapy");
        liveProfile.setYearsExperience(8);
        liveProfile.setBio("Internal therapist biography");
        liveProfile.setAcceptingClients(true);
        liveProfile.setProfileImageUrl("/uploads/therapists/jane.jpg");
        liveProfile.setPublicBio(List.of(
                "Jane is an experienced therapist.",
                "She works with adults and young people."
        ));
        liveProfile.setLanguages(List.of("English", "Irish"));
        liveProfile.setSpecialisms(List.of("Anxiety", "Trauma"));

        when(userRepository.findById(therapistId)).thenReturn(Optional.of(therapist));
        when(submissionRepository.findFirstByTherapistIdAndStatusInOrderByUpdatedAtDesc(
                eq(therapistId), anyCollection()
        )).thenReturn(Optional.empty());
        when(profileRepository.findById(therapistId)).thenReturn(Optional.of(liveProfile));
        when(submissionRepository.saveAndFlush(any(TherapistProfileSubmission.class)))
                .thenAnswer(invocation -> {
                    TherapistProfileSubmission saved = invocation.getArgument(0);
                    saved.setId(submissionId);
                    saved.setVersion(0L);
                    return saved;
                });

        TherapistProfileSubmissionResponse result =
                submissionService.getOrCreateOwnDraft(therapistId);

        assertEquals(submissionId, result.submissionId());
        assertEquals(therapistId, result.userId());
        assertEquals("Jane", result.firstName());
        assertEquals("Smith", result.lastName());
        assertEquals("therapist@example.com", result.email());
        assertEquals("MSc Counselling and Psychotherapy", result.qualifications());
        assertEquals("REG-12345", result.registrationNumber());
        assertEquals(8, result.yearsExperience());
        assertEquals("Internal therapist biography", result.bio());
        assertTrue(result.acceptingClients());
        assertEquals("/uploads/therapists/jane.jpg", result.profileImageUrl());
        assertEquals(liveProfile.getPublicBio(), result.publicBio());
        assertEquals(liveProfile.getLanguages(), result.languages());
        assertEquals(liveProfile.getSpecialisms(), result.specialisms());
        assertEquals(TherapistProfileSubmissionStatus.DRAFT, result.status());

        ArgumentCaptor<TherapistProfileSubmission> captor =
                ArgumentCaptor.forClass(TherapistProfileSubmission.class);
        verify(submissionRepository).saveAndFlush(captor.capture());
        TherapistProfileSubmission savedDraft = captor.getValue();

        assertEquals(therapist, savedDraft.getTherapist());
        assertEquals(liveProfile.getQualifications(), savedDraft.getQualifications());
        assertEquals(liveProfile.getRegistrationNumber(), savedDraft.getRegistrationNumber());
        assertEquals(liveProfile.getYearsExperience(), savedDraft.getYearsExperience());
        assertEquals(liveProfile.getBio(), savedDraft.getBio());
        assertEquals(liveProfile.getAcceptingClients(), savedDraft.getAcceptingClients());
        assertEquals(liveProfile.getProfileImageUrl(), savedDraft.getProfileImageUrl());
        assertEquals(liveProfile.getPublicBio(), savedDraft.getPublicBio());
        assertEquals(liveProfile.getLanguages(), savedDraft.getLanguages());
        assertEquals(liveProfile.getSpecialisms(), savedDraft.getSpecialisms());
        assertEquals(TherapistProfileSubmissionStatus.DRAFT, savedDraft.getStatus());

        verify(communicationService, never()).notifyAdminsOfProfileSubmission(any(), any());
    }

    // JUNIT-PROFILE-002
    @Test
    void getOrCreateOwnDraft_existingDraft_returnsExistingDraft() {
        UUID therapistId = UUID.randomUUID();
        UUID submissionId = UUID.randomUUID();
        User therapist = createUser(
                therapistId,
                "therapist@example.com",
                "Jane",
                "Smith",
                UserRole.THERAPIST
        );

        TherapistProfileSubmission existingDraft = createSubmission(
                submissionId,
                therapist,
                TherapistProfileSubmissionStatus.DRAFT,
                2L
        );
        existingDraft.setQualifications("BA Counselling");
        existingDraft.setRegistrationNumber("REG-98765");
        existingDraft.setYearsExperience(6);
        existingDraft.setBio("Existing draft biography");
        existingDraft.setAcceptingClients(false);
        existingDraft.setProfileImageUrl("/uploads/therapists/draft-image.jpg");
        existingDraft.setPublicBio(List.of("Draft public biography paragraph."));
        existingDraft.setLanguages(List.of("English"));
        existingDraft.setSpecialisms(List.of("Stress", "Bereavement"));

        when(userRepository.findById(therapistId)).thenReturn(Optional.of(therapist));
        when(submissionRepository.findFirstByTherapistIdAndStatusInOrderByUpdatedAtDesc(
                eq(therapistId), anyCollection()
        )).thenReturn(Optional.of(existingDraft));

        TherapistProfileSubmissionResponse result =
                submissionService.getOrCreateOwnDraft(therapistId);

        assertEquals(submissionId, result.submissionId());
        assertEquals(therapistId, result.userId());
        assertEquals("BA Counselling", result.qualifications());
        assertEquals("REG-98765", result.registrationNumber());
        assertEquals(6, result.yearsExperience());
        assertEquals("Existing draft biography", result.bio());
        assertFalse(result.acceptingClients());
        assertEquals("/uploads/therapists/draft-image.jpg", result.profileImageUrl());
        assertEquals(List.of("Draft public biography paragraph."), result.publicBio());
        assertEquals(List.of("English"), result.languages());
        assertEquals(List.of("Stress", "Bereavement"), result.specialisms());
        assertEquals(TherapistProfileSubmissionStatus.DRAFT, result.status());
        assertEquals(2L, result.version());

        verify(profileRepository, never()).findById(any());
        verify(submissionRepository, never()).saveAndFlush(any());
        verifyNoInteractions(communicationService);
    }

    // JUNIT-PROFILE-003
    @Test
    void updateOwnDraft_validRequest_updatesDraft() {
        UUID therapistId = UUID.randomUUID();
        UUID submissionId = UUID.randomUUID();
        User therapist = createUser(
                therapistId,
                "therapist@example.com",
                "Jane",
                "Smith",
                UserRole.THERAPIST
        );
        TherapistProfileSubmission existingDraft = createSubmission(
                submissionId,
                therapist,
                TherapistProfileSubmissionStatus.DRAFT,
                3L
        );

        UpdateTherapistProfileDraftRequest request = new UpdateTherapistProfileDraftRequest(
                "  MSc Counselling and Psychotherapy  ",
                "  REG-54321  ",
                9,
                "  Updated internal biography  ",
                true,
                "  /uploads/therapists/updated-image.jpg  ",
                Arrays.asList("  Updated public biography  ", "", null, "  Second paragraph  "),
                Arrays.asList(" English ", "", null, " Irish "),
                Arrays.asList(" Anxiety ", null, " Trauma "),
                3L
        );

        when(userRepository.findById(therapistId)).thenReturn(Optional.of(therapist));
        when(submissionRepository.findFirstByTherapistIdAndStatusInOrderByUpdatedAtDesc(
                eq(therapistId), anyCollection()
        )).thenReturn(Optional.of(existingDraft));
        when(submissionRepository.saveAndFlush(existingDraft)).thenReturn(existingDraft);

        TherapistProfileSubmissionResponse result =
                submissionService.updateOwnDraft(therapistId, request);

        assertEquals("MSc Counselling and Psychotherapy", existingDraft.getQualifications());
        assertEquals("REG-54321", existingDraft.getRegistrationNumber());
        assertEquals(9, existingDraft.getYearsExperience());
        assertEquals("Updated internal biography", existingDraft.getBio());
        assertTrue(existingDraft.getAcceptingClients());
        assertEquals("/uploads/therapists/updated-image.jpg", existingDraft.getProfileImageUrl());
        assertEquals(List.of("Updated public biography", "Second paragraph"), existingDraft.getPublicBio());
        assertEquals(List.of("English", "Irish"), existingDraft.getLanguages());
        assertEquals(List.of("Anxiety", "Trauma"), existingDraft.getSpecialisms());
        assertEquals(TherapistProfileSubmissionStatus.DRAFT, existingDraft.getStatus());

        assertEquals(submissionId, result.submissionId());
        assertEquals("MSc Counselling and Psychotherapy", result.qualifications());
        assertEquals("REG-54321", result.registrationNumber());
        assertEquals(List.of("English", "Irish"), result.languages());
        assertEquals(TherapistProfileSubmissionStatus.DRAFT, result.status());

        verify(submissionRepository).saveAndFlush(existingDraft);
        verifyNoInteractions(profileRepository);
        verifyNoInteractions(communicationService);
    }

    // JUNIT-PROFILE-004
    @Test
    void updateOwnDraft_nonDraftStatus_resetsWorkflowToDraft() {
        UUID therapistId = UUID.randomUUID();
        UUID reviewerId = UUID.randomUUID();
        User therapist = createUser(therapistId, "therapist@example.com", "Jane", "Smith", UserRole.THERAPIST);
        User reviewer = createUser(reviewerId, "admin@example.com", "Alex", "Admin", UserRole.ADMIN);

        TherapistProfileSubmission submission = createSubmission(
                UUID.randomUUID(), therapist, TherapistProfileSubmissionStatus.CHANGES_REQUESTED, 4L
        );
        submission.setReviewNotes("Please expand the biography");
        submission.setReviewedBy(reviewer);
        submission.setReviewedAt(Instant.parse("2026-07-20T10:00:00Z"));

        UpdateTherapistProfileDraftRequest request = new UpdateTherapistProfileDraftRequest(
                "Updated qualification", "REG-400", 4, "Updated bio", true,
                null, List.of("Updated public bio"), List.of("English"),
                List.of("Anxiety"), 4L
        );

        when(userRepository.findById(therapistId)).thenReturn(Optional.of(therapist));
        when(submissionRepository.findFirstByTherapistIdAndStatusInOrderByUpdatedAtDesc(
                eq(therapistId), anyCollection()
        )).thenReturn(Optional.of(submission));
        when(submissionRepository.saveAndFlush(submission)).thenReturn(submission);

        TherapistProfileSubmissionResponse result =
                submissionService.updateOwnDraft(therapistId, request);

        assertEquals(TherapistProfileSubmissionStatus.DRAFT, submission.getStatus());
        assertNull(submission.getReviewNotes());
        assertNull(submission.getReviewedBy());
        assertNull(submission.getReviewedAt());
        assertEquals(TherapistProfileSubmissionStatus.DRAFT, result.status());
        assertNull(result.reviewNotes());
        assertNull(result.reviewedAt());

        verify(submissionRepository).saveAndFlush(submission);
        verifyNoInteractions(profileRepository);
        verifyNoInteractions(communicationService);
    }

    // JUNIT-PROFILE-005
    @Test
    void submitOwnDraft_validDraft_submitsSuccessfully() {
        UUID therapistId = UUID.randomUUID();
        UUID submissionId = UUID.randomUUID();
        User therapist = createUser(therapistId, "therapist@example.com", "Jane", "Smith", UserRole.THERAPIST);
        TherapistProfileSubmission draft = createSubmission(
                submissionId, therapist, TherapistProfileSubmissionStatus.DRAFT, 5L
        );
        draft.setReviewNotes("Old note");
        draft.setReviewedBy(createUser(UUID.randomUUID(), "old-admin@example.com", "Old", "Admin", UserRole.ADMIN));
        draft.setReviewedAt(Instant.parse("2026-07-20T10:00:00Z"));

        when(userRepository.findById(therapistId)).thenReturn(Optional.of(therapist));
        when(submissionRepository.findFirstByTherapistIdAndStatusInOrderByUpdatedAtDesc(
                eq(therapistId), anyCollection()
        )).thenReturn(Optional.of(draft));
        when(profileRepository.existsByRegistrationNumberAndUserIdNot("REG-100", therapistId))
                .thenReturn(false);
        when(submissionRepository.saveAndFlush(draft)).thenReturn(draft);

        TherapistProfileSubmissionResponse result = submissionService.submitOwnDraft(
                therapistId,
                new SubmitTherapistProfileDraftRequest(5L)
        );

        assertEquals(TherapistProfileSubmissionStatus.SUBMITTED, draft.getStatus());
        assertNotNull(draft.getSubmittedAt());
        assertNull(draft.getReviewNotes());
        assertNull(draft.getReviewedBy());
        assertNull(draft.getReviewedAt());
        assertEquals(TherapistProfileSubmissionStatus.SUBMITTED, result.status());
        assertEquals(draft.getSubmittedAt(), result.submittedAt());

        verify(submissionRepository).saveAndFlush(draft);
        verify(communicationService).notifyAdminsOfProfileSubmission(therapist, submissionId);
    }

    // JUNIT-PROFILE-006
    @Test
    void submitOwnDraft_duplicateRegistrationNumber_throwsConflictException() {
        UUID therapistId = UUID.randomUUID();
        User therapist = createUser(therapistId, "therapist@example.com", "Jane", "Smith", UserRole.THERAPIST);
        TherapistProfileSubmission draft = createSubmission(
                UUID.randomUUID(), therapist, TherapistProfileSubmissionStatus.DRAFT, 1L
        );
        draft.setRegistrationNumber("  REG-DUPLICATE  ");

        when(userRepository.findById(therapistId)).thenReturn(Optional.of(therapist));
        when(submissionRepository.findFirstByTherapistIdAndStatusInOrderByUpdatedAtDesc(
                eq(therapistId), anyCollection()
        )).thenReturn(Optional.of(draft));
        when(profileRepository.existsByRegistrationNumberAndUserIdNot("REG-DUPLICATE", therapistId))
                .thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> submissionService.submitOwnDraft(
                        therapistId,
                        new SubmitTherapistProfileDraftRequest(1L)
                )
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("That registration number is already in use", exception.getReason());
        assertEquals(TherapistProfileSubmissionStatus.DRAFT, draft.getStatus());
        assertNull(draft.getSubmittedAt());

        verify(submissionRepository, never()).saveAndFlush(any());
        verifyNoInteractions(communicationService);
    }

    // JUNIT-PROFILE-007
    @Test
    void approveSubmission_updatesLiveProfileAndMarksApproved() {
        UUID adminId = UUID.randomUUID();
        UUID therapistId = UUID.randomUUID();
        UUID submissionId = UUID.randomUUID();
        User admin = createUser(adminId, "admin@example.com", "Alex", "Admin", UserRole.ADMIN);
        User therapist = createUser(therapistId, "therapist@example.com", "Jane", "Smith", UserRole.THERAPIST);
        TherapistProfileSubmission submission = createSubmission(
                submissionId, therapist, TherapistProfileSubmissionStatus.SUBMITTED, 6L
        );
        submission.setQualifications("New MSc Qualification");
        submission.setRegistrationNumber("REG-NEW");
        submission.setYearsExperience(12);
        submission.setBio("New internal biography");
        submission.setAcceptingClients(false);
        submission.setProfileImageUrl("/uploads/new.jpg");
        submission.setPublicBio(List.of("New public biography"));
        submission.setLanguages(List.of("English", "Irish"));
        submission.setSpecialisms(List.of("Trauma", "Anxiety"));

        TherapistProfile liveProfile = createProfile(therapist, "REG-OLD");

        when(userRepository.findById(adminId)).thenReturn(Optional.of(admin));
        when(submissionRepository.findById(submissionId)).thenReturn(Optional.of(submission));
        when(profileRepository.findById(therapistId)).thenReturn(Optional.of(liveProfile));
        when(profileRepository.existsByRegistrationNumberAndUserIdNot("REG-NEW", therapistId))
                .thenReturn(false);
        when(profileRepository.saveAndFlush(liveProfile)).thenReturn(liveProfile);
        when(submissionRepository.saveAndFlush(submission)).thenReturn(submission);

        AdminTherapistProfileSubmissionResponse result = submissionService.approve(
                adminId,
                submissionId,
                new AdminTherapistProfileApproveRequest(6L, "  Approved profile  ")
        );

        assertEquals("New MSc Qualification", liveProfile.getQualifications());
        assertEquals("REG-NEW", liveProfile.getRegistrationNumber());
        assertEquals(12, liveProfile.getYearsExperience());
        assertEquals("New internal biography", liveProfile.getBio());
        assertFalse(liveProfile.getAcceptingClients());
        assertEquals("/uploads/new.jpg", liveProfile.getProfileImageUrl());
        assertEquals(List.of("New public biography"), liveProfile.getPublicBio());
        assertEquals(List.of("English", "Irish"), liveProfile.getLanguages());
        assertEquals(List.of("Trauma", "Anxiety"), liveProfile.getSpecialisms());

        assertEquals(TherapistProfileSubmissionStatus.APPROVED, submission.getStatus());
        assertEquals("Approved profile", submission.getReviewNotes());
        assertEquals(admin, submission.getReviewedBy());
        assertNotNull(submission.getReviewedAt());
        assertNotNull(submission.getApprovedAt());
        assertEquals(submission.getReviewedAt(), submission.getApprovedAt());
        assertEquals(TherapistProfileSubmissionStatus.APPROVED, result.status());
        assertEquals("Alex Admin", result.reviewedByName());

        verify(profileRepository).saveAndFlush(liveProfile);
        verify(submissionRepository).saveAndFlush(submission);
        verify(communicationService).notifyTherapistOfProfileDecision(
                therapist, submissionId, "APPROVED", "Approved profile"
        );
    }

    // JUNIT-PROFILE-008
    @Test
    void requestChanges_validFeedback_marksSubmission() {
        UUID adminId = UUID.randomUUID();
        UUID therapistId = UUID.randomUUID();
        UUID submissionId = UUID.randomUUID();
        User admin = createUser(adminId, "admin@example.com", "Alex", "Admin", UserRole.ADMIN);
        User therapist = createUser(therapistId, "therapist@example.com", "Jane", "Smith", UserRole.THERAPIST);
        TherapistProfileSubmission submission = createSubmission(
                submissionId, therapist, TherapistProfileSubmissionStatus.SUBMITTED, 2L
        );
        TherapistProfile liveProfile = createProfile(therapist, "REG-LIVE");

        when(userRepository.findById(adminId)).thenReturn(Optional.of(admin));
        when(submissionRepository.findById(submissionId)).thenReturn(Optional.of(submission));
        when(submissionRepository.saveAndFlush(submission)).thenReturn(submission);
        when(profileRepository.findById(therapistId)).thenReturn(Optional.of(liveProfile));

        AdminTherapistProfileSubmissionResponse result = submissionService.requestChanges(
                adminId,
                submissionId,
                new AdminTherapistProfileReviewRequest(2L, "  Please expand the public biography  ")
        );

        assertEquals(TherapistProfileSubmissionStatus.CHANGES_REQUESTED, submission.getStatus());
        assertEquals("Please expand the public biography", submission.getReviewNotes());
        assertEquals(admin, submission.getReviewedBy());
        assertNotNull(submission.getReviewedAt());
        assertEquals(TherapistProfileSubmissionStatus.CHANGES_REQUESTED, result.status());
        assertEquals("Please expand the public biography", result.reviewNotes());

        verify(submissionRepository).saveAndFlush(submission);
        verify(profileRepository, never()).saveAndFlush(any());
        verifyNoInteractions(communicationService);
    }

    // JUNIT-PROFILE-009
    @Test
    void rejectSubmission_validFeedback_marksRejected() {
        UUID adminId = UUID.randomUUID();
        UUID therapistId = UUID.randomUUID();
        UUID submissionId = UUID.randomUUID();
        User admin = createUser(adminId, "admin@example.com", "Alex", "Admin", UserRole.ADMIN);
        User therapist = createUser(therapistId, "therapist@example.com", "Jane", "Smith", UserRole.THERAPIST);
        TherapistProfileSubmission submission = createSubmission(
                submissionId, therapist, TherapistProfileSubmissionStatus.SUBMITTED, 3L
        );
        TherapistProfile liveProfile = createProfile(therapist, "REG-LIVE");

        when(userRepository.findById(adminId)).thenReturn(Optional.of(admin));
        when(submissionRepository.findById(submissionId)).thenReturn(Optional.of(submission));
        when(submissionRepository.saveAndFlush(submission)).thenReturn(submission);
        when(profileRepository.findById(therapistId)).thenReturn(Optional.of(liveProfile));

        AdminTherapistProfileSubmissionResponse result = submissionService.reject(
                adminId,
                submissionId,
                new AdminTherapistProfileReviewRequest(3L, "  Registration evidence is missing  ")
        );

        assertEquals(TherapistProfileSubmissionStatus.REJECTED, submission.getStatus());
        assertEquals("Registration evidence is missing", submission.getReviewNotes());
        assertEquals(admin, submission.getReviewedBy());
        assertNotNull(submission.getReviewedAt());
        assertEquals(TherapistProfileSubmissionStatus.REJECTED, result.status());

        verify(profileRepository, never()).saveAndFlush(any());
        verify(communicationService).notifyTherapistOfProfileDecision(
                therapist,
                submissionId,
                "REJECTED",
                "Registration evidence is missing"
        );
    }

    // JUNIT-PROFILE-010
    @ParameterizedTest
    @EnumSource(
            value = TherapistProfileSubmissionStatus.class,
            names = {"DRAFT", "CHANGES_REQUESTED", "REJECTED", "APPROVED"}
    )
    void approveSubmission_invalidStatus_throwsException(
            TherapistProfileSubmissionStatus invalidStatus
    ) {
        UUID adminId = UUID.randomUUID();
        UUID submissionId = UUID.randomUUID();
        User admin = createUser(adminId, "admin@example.com", "Alex", "Admin", UserRole.ADMIN);
        User therapist = createUser(UUID.randomUUID(), "therapist@example.com", "Jane", "Smith", UserRole.THERAPIST);
        TherapistProfileSubmission submission = createSubmission(
                submissionId, therapist, invalidStatus, 1L
        );

        when(userRepository.findById(adminId)).thenReturn(Optional.of(admin));
        when(submissionRepository.findById(submissionId)).thenReturn(Optional.of(submission));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> submissionService.approve(
                        adminId,
                        submissionId,
                        new AdminTherapistProfileApproveRequest(1L, "Approved")
                )
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Only submitted profile changes can be reviewed", exception.getReason());
        assertEquals(invalidStatus, submission.getStatus());

        verifyNoInteractions(profileRepository);
        verify(submissionRepository, never()).saveAndFlush(any());
        verifyNoInteractions(communicationService);
    }

    private User createUser(
            UUID id,
            String email,
            String firstName,
            String lastName,
            UserRole role
    ) {
        User user = new User(email, "encoded-password", firstName, lastName, role);
        user.setId(id);
        user.setAccountStatus(AccountStatus.ACTIVE);
        return user;
    }

    private TherapistProfile createProfile(User therapist, String registrationNumber) {
        TherapistProfile profile = new TherapistProfile(
                therapist,
                "Current qualification",
                registrationNumber
        );
        profile.setUserId(therapist.getId());
        profile.setYearsExperience(5);
        profile.setBio("Current biography");
        profile.setAcceptingClients(true);
        profile.setProfileImageUrl("/uploads/current.jpg");
        profile.setPublicBio(List.of("Current public biography"));
        profile.setLanguages(List.of("English"));
        profile.setSpecialisms(List.of("Stress"));
        return profile;
    }

    private TherapistProfileSubmission createSubmission(
            UUID id,
            User therapist,
            TherapistProfileSubmissionStatus status,
            Long version
    ) {
        TherapistProfileSubmission submission = new TherapistProfileSubmission(therapist);
        submission.setId(id);
        submission.setQualifications("Current qualification");
        submission.setRegistrationNumber("REG-100");
        submission.setYearsExperience(5);
        submission.setBio("Current biography");
        submission.setAcceptingClients(true);
        submission.setProfileImageUrl("/uploads/current.jpg");
        submission.setPublicBio(List.of("Current public biography"));
        submission.setLanguages(List.of("English"));
        submission.setSpecialisms(List.of("Stress"));
        submission.setStatus(status);
        submission.setVersion(version);
        return submission;
    }
}
