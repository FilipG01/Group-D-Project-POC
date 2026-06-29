package com.roottherapy.backend.profile.admin.dto;

import com.roottherapy.backend.profile.admin.AdminProfile;

import java.time.Instant;
import java.util.UUID;

public record AdminProfileResponse(
        UUID userId,
        String jobTitle,
        String department,
        Instant createdAt,
        Instant updatedAt
){

    public static AdminProfileResponse from(AdminProfile profile){
        return new AdminProfileResponse(
                profile.getUserId(),
                profile.getJobTitle(),
                profile.getDepartment(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }
}

