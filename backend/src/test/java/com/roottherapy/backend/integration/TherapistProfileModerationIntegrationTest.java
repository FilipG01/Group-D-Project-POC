package com.roottherapy.backend.integration;

import com.roottherapy.backend.notification.ModerationCommunicationService;
import com.roottherapy.backend.profile.therapist.TherapistProfile;
import com.roottherapy.backend.profile.therapist.TherapistProfileRepository;
import com.roottherapy.backend.profile.therapist.submission.TherapistProfileSubmission;
import com.roottherapy.backend.profile.therapist.submission.TherapistProfileSubmissionRepository;
import com.roottherapy.backend.profile.therapist.submission.TherapistProfileSubmissionService;
import com.roottherapy.backend.profile.therapist.submission.TherapistProfileSubmissionStatus;
import com.roottherapy.backend.profile.therapist.submission.dto.AdminTherapistProfileApproveRequest;
import com.roottherapy.backend.users.User;
import com.roottherapy.backend.users.UserRepository;
import com.roottherapy.backend.users.UserRole;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@Transactional
class TherapistProfileModerationIntegrationTest {

    @Autowired
    private TherapistProfileSubmissionService submissionService;

    @Autowired
    private TherapistProfileSubmissionRepository submissionRepository;

    @Autowired
    private TherapistProfileRepository profileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @MockitoBean
    private ModerationCommunicationService communicationService;

    @Test
    void approveSubmission_updatesLiveProfileAndSubmissionStatus() {
        User therapist = userRepository.saveAndFlush(new User(
                "approval-therapist@example.com",
                "encoded-password",
                "Approval",
                "Therapist",
                UserRole.THERAPIST
        ));

        User admin = userRepository.saveAndFlush(new User(
                "approval-admin@example.com",
                "encoded-password",
                "Approval",
                "Admin",
                UserRole.ADMIN
        ));

        TherapistProfile liveProfile = new TherapistProfile(
                therapist,
                "Original qualification",
                "REG-APPROVAL-001"
        );
        liveProfile.setBio("Original internal biography");
        liveProfile.setPublicBio(List.of("Original public biography"));
        liveProfile.setLanguages(List.of("English"));
        liveProfile.setSpecialisms(List.of("Anxiety"));
        liveProfile.setYearsExperience(2);
        liveProfile.setAcceptingClients(true);
        liveProfile.setProfileImageUrl("/uploads/therapists/original.png");
        liveProfile.setDisplayOrder(1);
        liveProfile.setPubliclyVisible(false);
        profileRepository.saveAndFlush(liveProfile);

        TherapistProfileSubmission submission =
                new TherapistProfileSubmission(therapist);
        submission.setQualifications("Approved qualification");
        submission.setRegistrationNumber("REG-APPROVAL-002");
        submission.setYearsExperience(8);
        submission.setBio("Approved internal biography");
        submission.setAcceptingClients(false);
        submission.setProfileImageUrl("/uploads/therapists/approved.png");
        submission.setPublicBio(List.of(
                "Approved public biography paragraph one",
                "Approved public biography paragraph two"
        ));
        submission.setLanguages(List.of("English", "Polish"));
        submission.setSpecialisms(List.of("Trauma", "Grief"));
        submission.setStatus(TherapistProfileSubmissionStatus.SUBMITTED);
        submission.setSubmittedAt(Instant.now());

        submission = submissionRepository.saveAndFlush(submission);

        submissionService.approve(
                admin.getId(),
                submission.getId(),
                new AdminTherapistProfileApproveRequest(
                        submission.getVersion(),
                        "Approved after review"
                )
        );

        // Clear the persistence context so assertions reload committed entity
        // state from PostgreSQL rather than using cached objects.
        entityManager.clear();

        TherapistProfile updatedProfile = profileRepository.findById(
                therapist.getId()
        ).orElseThrow();

        TherapistProfileSubmission approvedSubmission =
                submissionRepository.findById(
                        submission.getId()
                ).orElseThrow();

        assertAll(
                () -> assertEquals(
                        "Approved qualification",
                        updatedProfile.getQualifications()
                ),
                () -> assertEquals(
                        "REG-APPROVAL-002",
                        updatedProfile.getRegistrationNumber()
                ),
                () -> assertEquals(
                        8,
                        updatedProfile.getYearsExperience()
                ),
                () -> assertEquals(
                        "Approved internal biography",
                        updatedProfile.getBio()
                ),
                () -> assertFalse(updatedProfile.getAcceptingClients()),
                () -> assertEquals(
                        "/uploads/therapists/approved.png",
                        updatedProfile.getProfileImageUrl()
                ),
                () -> assertEquals(
                        List.of(
                                "Approved public biography paragraph one",
                                "Approved public biography paragraph two"
                        ),
                        updatedProfile.getPublicBio()
                ),
                () -> assertEquals(
                        List.of("English", "Polish"),
                        updatedProfile.getLanguages()
                ),
                () -> assertEquals(
                        List.of("Trauma", "Grief"),
                        updatedProfile.getSpecialisms()
                ),
                () -> assertEquals(
                        TherapistProfileSubmissionStatus.APPROVED,
                        approvedSubmission.getStatus()
                ),
                () -> assertEquals(
                        "Approved after review",
                        approvedSubmission.getReviewNotes()
                ),
                () -> assertEquals(
                        admin.getId(),
                        approvedSubmission.getReviewedBy().getId()
                ),
                () -> assertNotNull(
                        approvedSubmission.getReviewedAt()
                ),
                () -> assertNotNull(
                        approvedSubmission.getApprovedAt()
                )
        );

        verify(communicationService)
                .notifyTherapistOfProfileDecision(
                        any(User.class),
                        eq(approvedSubmission.getId()),
                        eq("APPROVED"),
                        eq("Approved after review")
                );
    }
}
