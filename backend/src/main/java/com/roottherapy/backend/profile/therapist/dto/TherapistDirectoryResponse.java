package com.roottherapy.backend.profile.therapist.dto;


import com.roottherapy.backend.profile.therapist.TherapistProfile;
import java.util.UUID;

public record TherapistDirectoryResponse(
    UUID userId,
    String firstName,
    String lastName,
    String qualifications,
    Integer yearsExperience,
    String bio,
    Boolean acceptingClients
){    
    public static TherapistDirectoryResponse from(TherapistProfile profile){
        return new TherapistDirectoryResponse(
            profile.getUserId(),
            profile.getUser().getFirstName(),
            profile.getUser().getLastName(),
            profile.getQualifications(),
            profile.getYearsExperience(),
            profile.getBio(),
            profile.getAcceptingClients()
        );
    }
}
