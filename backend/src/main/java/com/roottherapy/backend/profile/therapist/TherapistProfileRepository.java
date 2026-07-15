package com.roottherapy.backend.profile.therapist;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TherapistProfileRepository
        extends JpaRepository<TherapistProfile, UUID> {

    boolean existsByRegistrationNumber(String registrationNumber);

    boolean existsByRegistrationNumberAndUserIdNot(
            String registrationNumber,
            UUID userId
    );

    /*
     * Existing client-facing directory.
     * Only therapists currently accepting clients are returned.
     */
    List<TherapistProfile>
    findByAcceptingClientsTrueOrderByUserLastNameAscUserFirstNameAsc();

    Optional<TherapistProfile>
    findByUserIdAndAcceptingClientsTrue(UUID userId);

    /*
     * Public About page.
     * A therapist can remain publicly visible even when they are
     * temporarily not accepting new clients.
     */
    List<TherapistProfile>
    findByPubliclyVisibleTrueOrderByDisplayOrderAscUserLastNameAsc();

    Optional<TherapistProfile>
    findByUserIdAndPubliclyVisibleTrue(UUID userId);

    /*
     * Admin dashboard.
     * Returns every therapist profile, including hidden profiles.
     */
    List<TherapistProfile>
    findAllByOrderByDisplayOrderAscUserLastNameAsc();
}