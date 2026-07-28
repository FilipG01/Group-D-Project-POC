package com.roottherapy.backend.appointments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CancelAppointmentRequest(
        @NotBlank(message = "Cancellation reason is needed!")
        @Size(max = 2000)
        String cancellationReason
) {
}
