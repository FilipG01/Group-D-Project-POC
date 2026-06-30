package com.roottherapy.backend.profile.therapist;

import com.roottherapy.backend.profile.therapist.dto.TherapistDirectoryResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

public interface TherapistProfileRepository extends JpaRepository<TherapistProfile, UUID> {
    boolean existsByRegistrationNumber(String registrationNumber);
    List<TherapistProfile> findByAcceptingClientsTrue();
    Optional<TherapistProfile> findByUserIdAndAcceptingClientsTrue(UUID userId);

}
