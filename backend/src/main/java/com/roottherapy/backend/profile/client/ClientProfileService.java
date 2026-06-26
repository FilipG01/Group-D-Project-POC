package com.roottherapy.backend.profile.client;


import com.roottherapy.backend.profile.client.dto.ClientProfileResponse;
import com.roottherapy.backend.profile.client.dto.UpdateClientProfileRequest;
import com.roottherapy.backend.users.User;
import com.roottherapy.backend.users.UserRole;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ClientProfileService {

    private final ClientProfileRepository clientProfileRepository;

    public  ClientProfileService(ClientProfileRepository clientProfileRepository) {
        this.clientProfileRepository = clientProfileRepository;
    }

    public ClientProfileResponse getMyProfile(User user){
        ClientProfile profile = getOrCreateClientProfile(user);
        return ClientProfileResponse.from(profile);
    }

    public ClientProfileResponse updateMyProfile(User user, UpdateClientProfileRequest req){

        ClientProfile profile = getOrCreateClientProfile(user);
        profile.setDateOfBirth(req.dateOfBirth());
        profile.setTherapyGoalsSummary(req.therapyGoalsSummary());
        profile.setPreferredContactMethod(req.preferredContactMethod());

        ClientProfile savedProfile = clientProfileRepository.save(profile);
        return ClientProfileResponse.from(savedProfile);
    }

    public ClientProfile getOrCreateClientProfile(User user){
        if (user.getRole() != UserRole.CLIENT){
            throw new AccessDeniedException("Only clients have access the client profiles!");
        }
        return clientProfileRepository.findById(user.getId())
                .orElseGet(() -> clientProfileRepository.save(new ClientProfile(user)));

    }

}
