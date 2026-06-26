package com.roottherapy.backend.profile.client;


import com.roottherapy.backend.profile.client.dto.ClientProfileResponse;
import com.roottherapy.backend.profile.client.dto.UpdateClientProfileRequest;
import com.roottherapy.backend.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/client-profile")
public class ClientProfileController {

    private final ClientProfileService  clientProfileService;

    public ClientProfileController(ClientProfileService clientProfileService){
        this.clientProfileService = clientProfileService;
    }

    @GetMapping("/me")
    public ClientProfileResponse getMyProfile(Authentication auth){
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        return clientProfileService.getMyProfile(userDetails.getUser());
    }

    @PutMapping("/me")
    public ClientProfileResponse updateMyProfile(Authentication auth, @Valid @RequestBody UpdateClientProfileRequest req){
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        return clientProfileService.updateMyProfile(userDetails.getUser(), req);
    }



}
