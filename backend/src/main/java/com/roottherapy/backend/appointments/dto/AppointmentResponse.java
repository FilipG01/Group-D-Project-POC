package com.roottherapy.backend.appointments.dto;

import com.roottherapy.backend.appointments.Appointment;
import com.roottherapy.backend.appointments.AppointmentLocationType;
import com.roottherapy.backend.appointments.AppointmentStatus;

import java.time.Instant;
import java.util.UUID;

public record AppointmentResponse(
        UUID id,
        UUID clientUserId,
        String clientFirstName,
        String clientLastName,
        UUID therapistUserId,
        String therapistFirstName,
        String therapistLastName,
        AppointmentStatus status,
        AppointmentLocationType locationType,
        Instant scheduledStart,
        Instant scheduledEnd,
        String clientNotes,
        String meetingLink,
        String cancellationReason,
        Instant cancelledAt,
        Instant createdAt,
        Instant updatedAt
) {

    public static AppointmentResponse from(Appointment appointment) {
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getClient().getId(),
                appointment.getClient().getFirstName(),
                appointment.getClient().getLastName(),
                appointment.getTherapist().getId(),
                appointment.getTherapist().getFirstName(),
                appointment.getTherapist().getLastName(),
                appointment.getStatus(),
                appointment.getLocationType(),
                appointment.getScheduledStart(),
                appointment.getScheduledEnd(),
                appointment.getClientNotes(),
                appointment.getMeetingLink(),
                appointment.getCancellationReason(),
                appointment.getCancelledAt(),
                appointment.getCreatedAt(),
                appointment.getUpdatedAt()
        );
    }
}
