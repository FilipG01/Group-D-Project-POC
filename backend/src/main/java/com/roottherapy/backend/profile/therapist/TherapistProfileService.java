package com.roottherapy.backend.profile.therapist;


import com.roottherapy.backend.profile.therapist.dto.TherapistProfileRequest;
import com.roottherapy.backend.profile.therapist.dto.TherapistProfileResponse;
import com.roottherapy.backend.users.User;
import com.roottherapy.backend.users.UserRole;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class TherapistProfileService {

    private final TherapistProfileRepository therapistProfileRepository;

    public TherapistProfileService(TherapistProfileRepository therapistProfileRepository) {
        this.therapistProfileRepository = therapistProfileRepository;
    }

    public TherapistProfileResponse getMyProfile(User user){
        ensureTherapist(user);

        TherapistProfile profile = therapistProfileRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Therapist profile does not exist!"));

        return TherapistProfileResponse.from(profile);
    }

    public TherapistProfileResponse updateMyProfile(User user, TherapistProfileRequest req){
        ensureTherapist(user);

        TherapistProfile profile = therapistProfileRepository.findById(user.getId())
                .orElseGet(() -> createProfile(user,req));

        if(!profile.getRegistrationNumber().equalsIgnoreCase(req.registrationNumber())
            && therapistProfileRepository.existsByRegistrationNumber(req.registrationNumber())){
            throw new IllegalStateException("Therapist profile already exists!");
        }
        profile.setQualifications(req.qualifications());
        profile.setRegistrationNumber(req.registrationNumber());
        profile.setYearsExperience(req.yearsExperience());
        profile.setBio(req.bio());
        profile.setAcceptingClients(req.acceptingClients());

        TherapistProfile savedProfile = therapistProfileRepository.save(profile);
        return TherapistProfileResponse.from(savedProfile);
    }

    private TherapistProfile createProfile(User user, TherapistProfileRequest req){
        if(therapistProfileRepository.existsByRegistrationNumber(req.registrationNumber())){
            throw new IllegalStateException("Therapist profile already exists!");
        }

        return new TherapistProfile(user,req.qualifications(), req.registrationNumber());
    }

    private void ensureTherapist(User user){
        if(user.getRole() != UserRole.THERAPIST){
            throw new AccessDeniedException("Only therapists can access therapist profiles!");
        }
    }
}
