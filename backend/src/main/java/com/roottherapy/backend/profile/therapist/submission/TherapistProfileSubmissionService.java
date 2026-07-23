package com.roottherapy.backend.profile.therapist.submission;

import com.roottherapy.backend.profile.therapist.TherapistProfile;
import com.roottherapy.backend.profile.therapist.TherapistProfileRepository;
import com.roottherapy.backend.profile.therapist.dto.AdminTherapistProfileResponse;
import com.roottherapy.backend.profile.therapist.submission.dto.*;
import com.roottherapy.backend.users.AccountStatus;
import com.roottherapy.backend.users.User;
import com.roottherapy.backend.users.UserRepository;
import com.roottherapy.backend.users.UserRole;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class TherapistProfileSubmissionService {

    private static final Set<TherapistProfileSubmissionStatus> ACTIVE_STATUSES = EnumSet.of(
            TherapistProfileSubmissionStatus.DRAFT,
            TherapistProfileSubmissionStatus.SUBMITTED,
            TherapistProfileSubmissionStatus.CHANGES_REQUESTED,
            TherapistProfileSubmissionStatus.REJECTED
    );

    private static final Set<TherapistProfileSubmissionStatus> EDITABLE_STATUSES = EnumSet.of(
            TherapistProfileSubmissionStatus.DRAFT,
            TherapistProfileSubmissionStatus.CHANGES_REQUESTED,
            TherapistProfileSubmissionStatus.REJECTED
    );

    private final TherapistProfileSubmissionRepository submissionRepository;
    private final TherapistProfileRepository profileRepository;
    private final UserRepository userRepository;

    public TherapistProfileSubmissionService(
            TherapistProfileSubmissionRepository submissionRepository,
            TherapistProfileRepository profileRepository,
            UserRepository userRepository
    ) {
        this.submissionRepository = submissionRepository;
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public TherapistProfileSubmissionResponse getOrCreateOwnDraft(UUID therapistUserId) {
        User therapist = getActiveUserWithRole(therapistUserId, UserRole.THERAPIST);

        return submissionRepository
                .findFirstByTherapistIdAndStatusInOrderByUpdatedAtDesc(therapistUserId, ACTIVE_STATUSES)
                .map(TherapistProfileSubmissionResponse::fromEntity)
                .orElseGet(() -> {
                    TherapistProfile liveProfile = getProfile(therapistUserId);
                    TherapistProfileSubmission draft = new TherapistProfileSubmission(therapist);
                    copyLiveProfileToSubmission(liveProfile, draft);
                    return TherapistProfileSubmissionResponse.fromEntity(submissionRepository.saveAndFlush(draft));
                });
    }

    @Transactional
    public TherapistProfileSubmissionResponse updateOwnDraft(
            UUID therapistUserId,
            UpdateTherapistProfileDraftRequest request
    ) {
        getActiveUserWithRole(therapistUserId, UserRole.THERAPIST);
        TherapistProfileSubmission draft = getActiveSubmission(therapistUserId);
        validateEditable(draft);
        validateVersion(draft, request.version());

        applyDraftChanges(draft, request);

        // Saving after rejection/requested changes starts a fresh editing cycle.
        if (draft.getStatus() != TherapistProfileSubmissionStatus.DRAFT) {
            draft.setStatus(TherapistProfileSubmissionStatus.DRAFT);
            draft.setReviewNotes(null);
            draft.setReviewedBy(null);
            draft.setReviewedAt(null);
        }

        return TherapistProfileSubmissionResponse.fromEntity(submissionRepository.saveAndFlush(draft));
    }

    @Transactional
    public TherapistProfileSubmissionResponse submitOwnDraft(
            UUID therapistUserId,
            SubmitTherapistProfileDraftRequest request
    ) {
        getActiveUserWithRole(therapistUserId, UserRole.THERAPIST);
        TherapistProfileSubmission draft = getActiveSubmission(therapistUserId);
        validateEditable(draft);
        validateVersion(draft, request.version());
        validateRegistrationNumber(draft.getRegistrationNumber(), therapistUserId);

        draft.setStatus(TherapistProfileSubmissionStatus.SUBMITTED);
        draft.setSubmittedAt(Instant.now());
        draft.setReviewNotes(null);
        draft.setReviewedBy(null);
        draft.setReviewedAt(null);

        return TherapistProfileSubmissionResponse.fromEntity(submissionRepository.saveAndFlush(draft));
    }

    @Transactional(readOnly = true)
    public List<AdminTherapistProfileSubmissionResponse> getAdminSubmissions(
            TherapistProfileSubmissionStatus status
    ) {
        List<TherapistProfileSubmission> submissions = status == null
                ? submissionRepository.findAllByOrderBySubmittedAtDescCreatedAtDesc()
                : submissionRepository.findByStatusOrderBySubmittedAtAsc(status);

        return submissions.stream().map(this::toAdminResponse).toList();
    }

    @Transactional(readOnly = true)
    public AdminTherapistProfileSubmissionResponse getAdminSubmission(UUID submissionId) {
        return toAdminResponse(getSubmission(submissionId));
    }

    @Transactional
    public AdminTherapistProfileSubmissionResponse approve(
            UUID adminUserId,
            UUID submissionId,
            AdminTherapistProfileApproveRequest request
    ) {
        User admin = getActiveUserWithRole(adminUserId, UserRole.ADMIN);
        TherapistProfileSubmission submission = getSubmission(submissionId);
        validateSubmitted(submission);
        validateVersion(submission, request.version());

        TherapistProfile liveProfile = getProfile(submission.getTherapist().getId());
        validateRegistrationNumber(submission.getRegistrationNumber(), liveProfile.getUserId());
        copySubmissionToLiveProfile(submission, liveProfile);
        profileRepository.saveAndFlush(liveProfile);

        Instant now = Instant.now();
        submission.setStatus(TherapistProfileSubmissionStatus.APPROVED);
        submission.setReviewNotes(cleanOptionalText(request.note()));
        submission.setReviewedBy(admin);
        submission.setReviewedAt(now);
        submission.setApprovedAt(now);
        TherapistProfileSubmission saved = submissionRepository.saveAndFlush(submission);

        return toAdminResponse(saved);
    }

    @Transactional
    public AdminTherapistProfileSubmissionResponse requestChanges(
            UUID adminUserId,
            UUID submissionId,
            AdminTherapistProfileReviewRequest request
    ) {
        return review(adminUserId, submissionId, request.version(), request.note(),
                TherapistProfileSubmissionStatus.CHANGES_REQUESTED);
    }

    @Transactional
    public AdminTherapistProfileSubmissionResponse reject(
            UUID adminUserId,
            UUID submissionId,
            AdminTherapistProfileReviewRequest request
    ) {
        return review(adminUserId, submissionId, request.version(), request.note(),
                TherapistProfileSubmissionStatus.REJECTED);
    }

    private AdminTherapistProfileSubmissionResponse review(
            UUID adminUserId,
            UUID submissionId,
            Long version,
            String note,
            TherapistProfileSubmissionStatus newStatus
    ) {
        User admin = getActiveUserWithRole(adminUserId, UserRole.ADMIN);
        TherapistProfileSubmission submission = getSubmission(submissionId);
        validateSubmitted(submission);
        validateVersion(submission, version);

        submission.setStatus(newStatus);
        submission.setReviewNotes(note.trim());
        submission.setReviewedBy(admin);
        submission.setReviewedAt(Instant.now());

        return toAdminResponse(submissionRepository.saveAndFlush(submission));
    }

    private AdminTherapistProfileSubmissionResponse toAdminResponse(TherapistProfileSubmission submission) {
        TherapistProfile liveProfile = getProfile(submission.getTherapist().getId());
        return AdminTherapistProfileSubmissionResponse.fromEntities(
                submission,
                AdminTherapistProfileResponse.fromEntity(liveProfile)
        );
    }

    private TherapistProfileSubmission getActiveSubmission(UUID therapistUserId) {
        return submissionRepository
                .findFirstByTherapistIdAndStatusInOrderByUpdatedAtDesc(therapistUserId, ACTIVE_STATUSES)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No active therapist profile draft was found"));
    }

    private TherapistProfileSubmission getSubmission(UUID submissionId) {
        return submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile submission not found"));
    }

    private TherapistProfile getProfile(UUID therapistUserId) {
        return profileRepository.findById(therapistUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Therapist profile not found"));
    }

    private User getActiveUserWithRole(UUID userId, UserRole requiredRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (user.getRole() != requiredRole) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to perform this action");
        }
        if (user.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This account is not active");
        }
        return user;
    }

    private void validateEditable(TherapistProfileSubmission submission) {
        if (!EDITABLE_STATUSES.contains(submission.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "This profile draft cannot be edited while its status is " + submission.getStatus());
        }
    }

    private void validateSubmitted(TherapistProfileSubmission submission) {
        if (submission.getStatus() != TherapistProfileSubmissionStatus.SUBMITTED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Only submitted profile changes can be reviewed");
        }
    }

    private void validateVersion(TherapistProfileSubmission submission, Long version) {
        if (!submission.getVersion().equals(version)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "This profile draft was changed elsewhere. Refresh the page and try again.");
        }
    }

    private void validateRegistrationNumber(String registrationNumber, UUID currentUserId) {
        if (profileRepository.existsByRegistrationNumberAndUserIdNot(registrationNumber.trim(), currentUserId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "That registration number is already in use");
        }
    }

    private void copyLiveProfileToSubmission(TherapistProfile profile, TherapistProfileSubmission submission) {
        submission.setQualifications(profile.getQualifications());
        submission.setRegistrationNumber(profile.getRegistrationNumber());
        submission.setYearsExperience(profile.getYearsExperience());
        submission.setBio(profile.getBio());
        submission.setAcceptingClients(profile.getAcceptingClients());
        submission.setProfileImageUrl(profile.getProfileImageUrl());
        submission.setPublicBio(List.copyOf(profile.getPublicBio()));
        submission.setLanguages(List.copyOf(profile.getLanguages()));
        submission.setSpecialisms(List.copyOf(profile.getSpecialisms()));
        submission.setStatus(TherapistProfileSubmissionStatus.DRAFT);
    }

    private void copySubmissionToLiveProfile(TherapistProfileSubmission source, TherapistProfile target) {
        target.setQualifications(source.getQualifications());
        target.setRegistrationNumber(source.getRegistrationNumber());
        target.setYearsExperience(source.getYearsExperience());
        target.setBio(source.getBio());
        target.setAcceptingClients(source.getAcceptingClients());
        target.setProfileImageUrl(source.getProfileImageUrl());
        target.setPublicBio(List.copyOf(source.getPublicBio()));
        target.setLanguages(List.copyOf(source.getLanguages()));
        target.setSpecialisms(List.copyOf(source.getSpecialisms()));
    }

    private void applyDraftChanges(TherapistProfileSubmission draft, UpdateTherapistProfileDraftRequest request) {
        draft.setQualifications(cleanRequiredText(request.qualifications()));
        draft.setRegistrationNumber(cleanRequiredText(request.registrationNumber()));
        draft.setYearsExperience(request.yearsExperience());
        draft.setBio(cleanOptionalText(request.bio()));
        draft.setAcceptingClients(Boolean.TRUE.equals(request.acceptingClients()));
        draft.setProfileImageUrl(cleanOptionalText(request.profileImageUrl()));
        draft.setPublicBio(cleanList(request.publicBio()));
        draft.setLanguages(cleanList(request.languages()));
        draft.setSpecialisms(cleanList(request.specialisms()));
    }

    private String cleanRequiredText(String value) {
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Required text cannot be blank");
        }
        return value.trim();
    }

    private String cleanOptionalText(String value) {
        if (value == null) return null;
        String cleaned = value.trim();
        return cleaned.isEmpty() ? null : cleaned;
    }

    private List<String> cleanList(List<String> values) {
        if (values == null) return List.of();
        return values.stream().filter(value -> value != null && !value.isBlank()).map(String::trim).toList();
    }
}
