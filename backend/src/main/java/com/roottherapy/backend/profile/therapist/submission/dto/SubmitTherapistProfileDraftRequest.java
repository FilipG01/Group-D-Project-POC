package com.roottherapy.backend.profile.therapist.submission.dto;

import jakarta.validation.constraints.NotNull;

public record SubmitTherapistProfileDraftRequest(@NotNull Long version) {}
