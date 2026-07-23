package com.roottherapy.backend.profile.therapist.submission.dto;

import com.roottherapy.backend.profile.therapist.submission.TherapistProfileSubmission;
import com.roottherapy.backend.profile.therapist.submission.TherapistProfileSubmissionStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record TherapistProfileSubmissionResponse(
        UUID submissionId,
        UUID userId,
        String firstName,
        String lastName,
        String email,
        String qualifications,
        String registrationNumber,
        Integer yearsExperience,
        String bio,
        Boolean acceptingClients,
        String profileImageUrl,
        List<String> publicBio,
        List<String> languages,
        List<String> specialisms,
        TherapistProfileSubmissionStatus status,
        String reviewNotes,
        Instant submittedAt,
        Instant reviewedAt,
        Instant createdAt,
        Instant updatedAt,
        Long version
) {
    public static TherapistProfileSubmissionResponse fromEntity(TherapistProfileSubmission submission) {
        return new TherapistProfileSubmissionResponse(
                submission.getId(),
                submission.getTherapist().getId(),
                submission.getTherapist().getFirstName(),
                submission.getTherapist().getLastName(),
                submission.getTherapist().getEmail(),
                submission.getQualifications(),
                submission.getRegistrationNumber(),
                submission.getYearsExperience(),
                submission.getBio(),
                submission.getAcceptingClients(),
                submission.getProfileImageUrl(),
                submission.getPublicBio(),
                submission.getLanguages(),
                submission.getSpecialisms(),
                submission.getStatus(),
                submission.getReviewNotes(),
                submission.getSubmittedAt(),
                submission.getReviewedAt(),
                submission.getCreatedAt(),
                submission.getUpdatedAt(),
                submission.getVersion()
        );
    }
}
