package com.roottherapy.backend.appointments.dto;

import com.roottherapy.backend.appointments.AppointmentLocationType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record CreateAppointmentRequest(
        @NotNull
        @Future
        Instant scheduledStart,

        @NotNull
        @Future
        Instant scheduledEnd,

        @NotNull
        AppointmentLocationType locationType,

        @Size(max = 2000)
        String clientNotes
) {
}
