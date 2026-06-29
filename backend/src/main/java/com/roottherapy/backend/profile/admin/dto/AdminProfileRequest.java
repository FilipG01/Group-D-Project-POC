package com.roottherapy.backend.profile.admin.dto;

import jakarta.validation.constraints.Size;

public record AdminProfileRequest(
        @Size(max = 150)
        String jobTitle,

        @Size(max = 150)
        String department
){}