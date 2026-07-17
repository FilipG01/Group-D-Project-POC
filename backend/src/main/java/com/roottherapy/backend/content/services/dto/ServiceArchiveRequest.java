package com.roottherapy.backend.content.services.dto;

import jakarta.validation.constraints.NotNull;

public record ServiceArchiveRequest(

        @NotNull(message = "Archived status is required")
        Boolean archived
) {
}