package com.roottherapy.backend.profile.client.dto;

import com.roottherapy.backend.profile.client.PreferredContactMethod;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateClientProfileRequest (

    @Past
    LocalDate dateOfBirth,

    @Size(max = 2000)
    String therapyGoalsSummary,

    PreferredContactMethod preferredContactMethod
){
}

