package com.roottherapy.backend.content.services.dto;

import jakarta.validation.constraints.NotNull;

public record ServicePublicationRequest(

        @NotNull(message = "Published status is required")
        Boolean published
) {
}