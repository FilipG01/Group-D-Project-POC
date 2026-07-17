package com.roottherapy.backend.profile.therapist.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdateOwnTherapistProfileRequest(

        @NotBlank(message = "Qualifications are required")
        String qualifications,

        @NotBlank(message = "Registration number is required")
        @Size(max = 100)
        String registrationNumber,

        @NotNull
        @Min(0)
        Integer yearsExperience,

        String bio,

        @NotNull
        Boolean acceptingClients,

        String profileImageUrl,

        @NotNull
        List<String> publicBio,

        @NotNull
        List<String> languages,

        @NotNull
        List<String> specialisms
) {
}