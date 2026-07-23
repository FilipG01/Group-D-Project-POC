package com.roottherapy.backend.profile.therapist.submission.dto;

import com.roottherapy.backend.profile.therapist.dto.AdminTherapistProfileResponse;
import com.roottherapy.backend.profile.therapist.submission.TherapistProfileSubmission;
import com.roottherapy.backend.profile.therapist.submission.TherapistProfileSubmissionStatus;

import java.time.Instant;
import java.util.UUID;

public record AdminTherapistProfileSubmissionResponse(
        UUID submissionId,
        TherapistProfileSubmissionStatus status,
        String reviewNotes,
        String reviewedByName,
        Instant submittedAt,
        Instant reviewedAt,
        Instant approvedAt,
        Instant createdAt,
        Instant updatedAt,
        Long version,
        AdminTherapistProfileResponse currentProfile,
        TherapistProfileSubmissionResponse proposedProfile
) {
    public static AdminTherapistProfileSubmissionResponse fromEntities(
            TherapistProfileSubmission submission,
            AdminTherapistProfileResponse currentProfile
    ) {
        String reviewer = submission.getReviewedBy() == null
                ? null
                : submission.getReviewedBy().getFirstName() + " " + submission.getReviewedBy().getLastName();

        return new AdminTherapistProfileSubmissionResponse(
                submission.getId(), submission.getStatus(), submission.getReviewNotes(), reviewer,
                submission.getSubmittedAt(), submission.getReviewedAt(), submission.getApprovedAt(),
                submission.getCreatedAt(), submission.getUpdatedAt(), submission.getVersion(),
                currentProfile, TherapistProfileSubmissionResponse.fromEntity(submission)
        );
    }
}
