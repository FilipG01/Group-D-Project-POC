package com.roottherapy.backend.profile.therapist;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TherapistProfileRepository extends JpaRepository<TherapistProfile, UUID> {
    boolean existsByRegistrationNumber(String registrationNumber);
}
