package com.roottherapy.backend.profile.therapist.submission.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdminTherapistProfileReviewRequest(
        @NotNull Long version,
        @NotBlank String note
) {}
