package com.roottherapy.backend.profile.therapist.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateTherapistRequest(

        @NotBlank(message = "First name is required")
        @Size(max = 100)
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 100)
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "A valid email address is required")
        @Size(max = 255)
        String email,

        @NotBlank(message = "A temporary password is required")
        @Size(
                min = 8,
                max = 100,
                message = "Password must be between 8 and 100 characters"
        )
        String temporaryPassword,

        String phoneNumber,

        @NotBlank(message = "Qualifications are required")
        String qualifications,

        @NotBlank(message = "Registration number is required")
        @Size(max = 100)
        String registrationNumber,

        @NotNull(message = "Years of experience is required")
        @Min(value = 0, message = "Years of experience cannot be negative")
        Integer yearsExperience
) {
}