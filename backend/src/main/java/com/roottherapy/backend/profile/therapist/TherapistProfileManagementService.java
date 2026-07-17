package com.roottherapy.backend.profile.therapist;

import com.roottherapy.backend.profile.therapist.dto.AdminTherapistProfileResponse;
import com.roottherapy.backend.profile.therapist.dto.AdminUpdateTherapistProfileRequest;
import com.roottherapy.backend.profile.therapist.dto.PublicTherapistProfileResponse;
import com.roottherapy.backend.profile.therapist.dto.UpdateOwnTherapistProfileRequest;
import com.roottherapy.backend.profile.therapist.dto.ReorderTherapistsRequest;
import com.roottherapy.backend.profile.therapist.dto.CreateTherapistRequest;
import com.roottherapy.backend.users.User;
import com.roottherapy.backend.users.UserRepository;
import com.roottherapy.backend.users.UserRole;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TherapistProfileManagementService {

    private final TherapistProfileRepository therapistProfileRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public TherapistProfileManagementService(
            TherapistProfileRepository therapistProfileRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.therapistProfileRepository = therapistProfileRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /*
     * Public About-page methods
     */

    @Transactional(readOnly = true)
    public List<PublicTherapistProfileResponse> getPublicTherapists() {
        return therapistProfileRepository
                .findByPubliclyVisibleTrueOrderByDisplayOrderAscUserLastNameAsc()
                .stream()
                .map(PublicTherapistProfileResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public PublicTherapistProfileResponse getPublicTherapist(UUID userId) {
        TherapistProfile profile = therapistProfileRepository
                .findByUserIdAndPubliclyVisibleTrue(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Therapist profile not found"
                ));

        return PublicTherapistProfileResponse.fromEntity(profile);
    }

    /*
     * Therapist self-management
     */

    @Transactional(readOnly = true)
    public AdminTherapistProfileResponse getOwnProfile(UUID userId) {
        TherapistProfile profile = findTherapistProfile(userId);

        return AdminTherapistProfileResponse.fromEntity(profile);
    }

    public AdminTherapistProfileResponse updateOwnProfile(
            UUID userId,
            UpdateOwnTherapistProfileRequest request
    ) {
        TherapistProfile profile = findTherapistProfile(userId);

        validateRegistrationNumber(
                request.registrationNumber(),
                userId
        );

        applySharedProfileChanges(
                profile,
                request.qualifications(),
                request.registrationNumber(),
                request.yearsExperience(),
                request.bio(),
                request.acceptingClients(),
                request.profileImageUrl(),
                request.publicBio(),
                request.languages(),
                request.specialisms()
        );

        TherapistProfile savedProfile =
                therapistProfileRepository.save(profile);

        return AdminTherapistProfileResponse.fromEntity(savedProfile);
    }

    /*
     * Admin management
     */

    @Transactional(readOnly = true)
    public List<AdminTherapistProfileResponse> getAllTherapistsForAdmin() {
        return therapistProfileRepository
                .findAllByOrderByDisplayOrderAscUserLastNameAsc()
                .stream()
                .map(AdminTherapistProfileResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminTherapistProfileResponse getTherapistForAdmin(
            UUID userId
    ) {
        return AdminTherapistProfileResponse.fromEntity(
                findTherapistProfile(userId)
        );
    }

    public AdminTherapistProfileResponse updateTherapistAsAdmin(
            UUID userId,
            AdminUpdateTherapistProfileRequest request
    ) {
        TherapistProfile profile = findTherapistProfile(userId);

        validateRegistrationNumber(
                request.registrationNumber(),
                userId
        );

        applySharedProfileChanges(
                profile,
                request.qualifications(),
                request.registrationNumber(),
                request.yearsExperience(),
                request.bio(),
                request.acceptingClients(),
                request.profileImageUrl(),
                request.publicBio(),
                request.languages(),
                request.specialisms()
        );

        profile.setDisplayOrder(request.displayOrder());
        profile.setPubliclyVisible(request.publiclyVisible());

        TherapistProfile savedProfile =
                therapistProfileRepository.save(profile);

        return AdminTherapistProfileResponse.fromEntity(savedProfile);
    }

    public AdminTherapistProfileResponse createTherapist(
            CreateTherapistRequest request
    ) {
        String email = request.email()
                .trim()
                .toLowerCase();

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "An account with this email already exists"
            );
        }

        String registrationNumber =
                cleanRequiredText(request.registrationNumber());

        if (
                therapistProfileRepository
                        .existsByRegistrationNumber(registrationNumber)
        ) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "That registration number is already in use"
            );
        }

        User therapistUser = new User(
                email,
                passwordEncoder.encode(request.temporaryPassword()),
                cleanRequiredText(request.firstName()),
                cleanRequiredText(request.lastName()),
                UserRole.THERAPIST
        );

        therapistUser.setPhoneNumber(
                cleanOptionalText(request.phoneNumber())
        );

        User savedUser = userRepository.save(therapistUser);

        TherapistProfile profile = new TherapistProfile(
                savedUser,
                cleanRequiredText(request.qualifications()),
                registrationNumber
        );

        profile.setYearsExperience(request.yearsExperience());
        profile.setAcceptingClients(false);
        profile.setPubliclyVisible(false);
        profile.setDisplayOrder(0);

        TherapistProfile savedProfile =
                therapistProfileRepository.save(profile);

        return AdminTherapistProfileResponse.fromEntity(savedProfile);
    }

    /*
     * Helpers
     */

    private TherapistProfile findTherapistProfile(UUID userId) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        if (user.getRole() != UserRole.THERAPIST) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "The selected user is not a therapist"
            );
        }

        return therapistProfileRepository
                .findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Therapist profile not found"
                ));
    }

    public List<AdminTherapistProfileResponse> reorderTherapists(
            ReorderTherapistsRequest request
    ) {
        for (var orderItem : request.therapists()) {
            TherapistProfile profile =
                    findTherapistProfile(orderItem.userId());

            profile.setDisplayOrder(orderItem.displayOrder());
        }

        /*
         * Because the service is transactional, Hibernate will save the
         * changed managed entities when the transaction finishes.
         */
        return therapistProfileRepository
                .findAllByOrderByDisplayOrderAscUserLastNameAsc()
                .stream()
                .map(AdminTherapistProfileResponse::fromEntity)
                .toList();
    }

    private void validateRegistrationNumber(
            String registrationNumber,
            UUID currentUserId
    ) {
        String cleanedRegistrationNumber =
                cleanRequiredText(registrationNumber);

        if (
                therapistProfileRepository
                        .existsByRegistrationNumberAndUserIdNot(
                                cleanedRegistrationNumber,
                                currentUserId
                        )
        ) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "That registration number is already in use"
            );
        }
    }

    private void applySharedProfileChanges(
            TherapistProfile profile,
            String qualifications,
            String registrationNumber,
            Integer yearsExperience,
            String bio,
            Boolean acceptingClients,
            String profileImageUrl,
            List<String> publicBio,
            List<String> languages,
            List<String> specialisms
    ) {
        profile.setQualifications(
                cleanRequiredText(qualifications)
        );

        profile.setRegistrationNumber(
                cleanRequiredText(registrationNumber)
        );

        profile.setYearsExperience(yearsExperience);
        profile.setBio(cleanOptionalText(bio));
        profile.setAcceptingClients(
                Boolean.TRUE.equals(acceptingClients)
        );

        profile.setProfileImageUrl(
                cleanOptionalText(profileImageUrl)
        );

        profile.setPublicBio(cleanList(publicBio));
        profile.setLanguages(cleanList(languages));
        profile.setSpecialisms(cleanList(specialisms));
    }

    private String cleanRequiredText(String value) {
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Required text cannot be blank"
            );
        }

        return value.trim();
    }

    private String cleanOptionalText(String value) {
        if (value == null) {
            return null;
        }

        String cleaned = value.trim();

        return cleaned.isEmpty() ? null : cleaned;
    }

    private List<String> cleanList(List<String> values) {
        if (values == null) {
            return List.of();
        }

        return values.stream()
                .filter(value -> value != null && !value.isBlank())
                .map(String::trim)
                .toList();
    }
}