package com.roottherapy.backend.content.services.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ServiceOrderItem(
        @NotNull Long id,

        @NotNull
        @Min(0)
        Integer displayOrder
) {
}