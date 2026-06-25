package com.roottherapy.backend.users.dto;

import com.roottherapy.backend.users.AccountStatus;
import com.roottherapy.backend.users.User;
import com.roottherapy.backend.users.UserRole;
import java.util.UUID;


//TODO: add createAt at the later date

public record UserResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        String phoneNumber,
        UserRole role,
        AccountStatus accountStatus
) {

    public static UserResponse from(User user){
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                user.getRole(),
                user.getAccountStatus()
        );
    }
}
