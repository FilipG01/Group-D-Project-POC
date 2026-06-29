package com.roottherapy.backend.profile.admin;

import com.roottherapy.backend.profile.admin.dto.AdminProfileRequest;
import com.roottherapy.backend.profile.admin.dto.AdminProfileResponse;
import com.roottherapy.backend.users.User;
import com.roottherapy.backend.users.UserRole;
import org.springframework.security.access.AccessDeniedException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AdminProfileService{

    private final AdminProfileRepository adminProfileRepository;

    public AdminProfileService(AdminProfileRepository adminProfileRepository){
        this.adminProfileRepository = adminProfileRepository;
    }

    public AdminProfileResponse getMyProfile(User user){
        AdminProfile profile = getOrCreateAdminProfile(user);
        return AdminProfileResponse.from(profile);
    }

    public AdminProfileResponse updateMyProfile(User user, AdminProfileRequest req){
        AdminProfile profile = getOrCreateAdminProfile(user);

        profile.setJobTitle(req.jobTitle());
        profile.setDepartment(req.department());

        AdminProfile savedProfile = adminProfileRepository.save(profile);
        return AdminProfileResponse.from(savedProfile);
    }

    private AdminProfile getOrCreateAdminProfile(User user){
        ensureAdmin(user);
        return adminProfileRepository.findById(user.getId())
                .orElseGet(() -> adminProfileRepository.save(new AdminProfile(user)));
    }

    private void ensureAdmin(User user){
        if(user.getRole() != UserRole.ADMIN){
            throw new AccessDeniedException("Only admin can access admin profiles!");
        }
    }
}