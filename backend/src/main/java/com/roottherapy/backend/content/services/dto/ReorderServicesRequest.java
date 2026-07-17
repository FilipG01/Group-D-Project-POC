package com.roottherapy.backend.content.services.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record ReorderServicesRequest(
        @NotEmpty
        List<@Valid ServiceOrderItem> services
) {
}