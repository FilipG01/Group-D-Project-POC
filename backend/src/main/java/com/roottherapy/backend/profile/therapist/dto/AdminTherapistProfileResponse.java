package com.roottherapy.backend.profile.therapist.dto;

import com.roottherapy.backend.profile.therapist.TherapistProfile;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record AdminTherapistProfileResponse(
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
        Integer displayOrder,
        Boolean publiclyVisible,
        Instant createdAt,
        Instant updatedAt
) {
    public static AdminTherapistProfileResponse fromEntity(
            TherapistProfile profile
    ) {
        return new AdminTherapistProfileResponse(
                profile.getUserId(),
                profile.getUser().getFirstName(),
                profile.getUser().getLastName(),
                profile.getUser().getEmail(),
                profile.getQualifications(),
                profile.getRegistrationNumber(),
                profile.getYearsExperience(),
                profile.getBio(),
                profile.getAcceptingClients(),
                profile.getProfileImageUrl(),
                profile.getPublicBio(),
                profile.getLanguages(),
                profile.getSpecialisms(),
                profile.getDisplayOrder(),
                profile.getPubliclyVisible(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }
}