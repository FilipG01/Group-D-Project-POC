package com.roottherapy.backend.profile.therapist.dto;

import com.roottherapy.backend.profile.therapist.TherapistProfile;

import java.time.Instant;
import java.util.UUID;

public record TherapistProfileResponse(
        UUID userId,
        String qualifications,
        String registrationNumber,
        Integer yearsExperience,
        String bio,
        Boolean acceptingClients,
        Instant createdAt,
        Instant updatedAt
) {

    public static TherapistProfileResponse from(TherapistProfile profile) {
        return new TherapistProfileResponse(
                profile.getUserId(),
                profile.getQualifications(),
                profile.getRegistrationNumber(),
                profile.getYearsExperience(),
                profile.getBio(),
                profile.getAcceptingClients(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()

        );
    }
}
