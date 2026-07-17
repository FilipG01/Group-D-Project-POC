package com.roottherapy.backend.profile.therapist.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record ReorderTherapistsRequest(

        @NotEmpty(message = "At least one therapist is required")
        List<@Valid TherapistOrderItem> therapists
) {
}