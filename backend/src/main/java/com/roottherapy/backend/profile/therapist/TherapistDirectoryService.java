package com.roottherapy.backend.profile.therapist;

import com.roottherapy.backend.profile.therapist.dto.TherapistDirectoryResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class TherapistDirectoryService {

    private final TherapistProfileRepository therapistProfileRepository;

    public TherapistDirectoryService(
            TherapistProfileRepository therapistProfileRepository
    ) {
        this.therapistProfileRepository = therapistProfileRepository;
    }

    public List<TherapistDirectoryResponse> listAvailableTherapists() {
        return therapistProfileRepository
                .findByAcceptingClientsTrueOrderByUserLastNameAscUserFirstNameAsc()
                .stream()
                .map(TherapistDirectoryResponse::from)
                .toList();
    }

    public TherapistDirectoryResponse getAvailableTherapist(
            UUID userId
    ) {
        TherapistProfile profile = therapistProfileRepository
                .findByUserIdAndAcceptingClientsTrue(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Therapist not found"
                ));

        return TherapistDirectoryResponse.from(profile);
    }
}