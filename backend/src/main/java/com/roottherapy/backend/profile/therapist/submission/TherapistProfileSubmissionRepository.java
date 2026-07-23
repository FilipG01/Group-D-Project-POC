package com.roottherapy.backend.profile.therapist.submission;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TherapistProfileSubmissionRepository extends JpaRepository<TherapistProfileSubmission, UUID> {
    Optional<TherapistProfileSubmission> findFirstByTherapistIdAndStatusInOrderByUpdatedAtDesc(
            UUID therapistId,
            Collection<TherapistProfileSubmissionStatus> statuses
    );

    Optional<TherapistProfileSubmission> findByIdAndTherapistId(UUID id, UUID therapistId);

    List<TherapistProfileSubmission> findByStatusOrderBySubmittedAtAsc(
            TherapistProfileSubmissionStatus status
    );

    List<TherapistProfileSubmission> findAllByOrderBySubmittedAtDescCreatedAtDesc();
}
