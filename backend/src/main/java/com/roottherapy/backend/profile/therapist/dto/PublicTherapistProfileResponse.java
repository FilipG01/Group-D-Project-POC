package com.roottherapy.backend.profile.therapist.dto;

import com.roottherapy.backend.profile.therapist.TherapistProfile;

import java.util.List;
import java.util.UUID;

public record PublicTherapistProfileResponse(
        UUID userId,
        String firstName,
        String lastName,
        String fullName,
        String profileImageUrl,
        List<String> publicBio,
        String qualifications,
        String registrationNumber,
        Integer yearsExperience,
        List<String> languages,
        List<String> specialisms,
        Boolean acceptingClients,
        Integer displayOrder
) {
    public static PublicTherapistProfileResponse fromEntity(
            TherapistProfile profile
    ) {
        String firstName = profile.getUser().getFirstName();
        String lastName = profile.getUser().getLastName();

        return new PublicTherapistProfileResponse(
                profile.getUserId(),
                firstName,
                lastName,
                firstName + " " + lastName,
                profile.getProfileImageUrl(),
                profile.getPublicBio(),
                profile.getQualifications(),
                profile.getRegistrationNumber(),
                profile.getYearsExperience(),
                profile.getLanguages(),
                profile.getSpecialisms(),
                profile.getAcceptingClients(),
                profile.getDisplayOrder()
        );
    }
}