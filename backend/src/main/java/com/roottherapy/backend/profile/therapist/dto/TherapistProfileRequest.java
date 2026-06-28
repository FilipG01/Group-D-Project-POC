package com.roottherapy.backend.profile.therapist.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TherapistProfileRequest(

        @NotBlank
        String qualifications,

        @NotBlank
        @Size(max = 100)
        String registrationNumber,

        @NotNull
        @Min(0)
        Integer yearsExperience,

        @Size(max = 2000)
        String bio,

        @NotNull
        Boolean acceptingClients
) {
}
