package com.roottherapy.backend.profile.client.dto;

import com.roottherapy.backend.profile.client.ClientProfile;
import com.roottherapy.backend.profile.client.PreferredContactMethod;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ClientProfileResponse (
        UUID userId,
        LocalDate dateOfBirth,
        String therapyGoalsSummary,
        PreferredContactMethod preferredContactMethod,
        Instant createdAt,
        Instant updatedAt
){

    public static ClientProfileResponse from(ClientProfile profile){
        return new ClientProfileResponse(
                profile.getUserId(),
                profile.getDateOfBirth(),
                profile.getTherapyGoalsSummary(),
                profile.getPreferredContactMethod(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }
}

