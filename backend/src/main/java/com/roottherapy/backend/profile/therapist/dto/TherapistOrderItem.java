package com.roottherapy.backend.profile.therapist.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record TherapistOrderItem(

        @NotNull(message = "Therapist ID is required")
        UUID userId,

        @NotNull(message = "Display order is required")
        @Min(value = 0, message = "Display order cannot be negative")
        Integer displayOrder
) {
}