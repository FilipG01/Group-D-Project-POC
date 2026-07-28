package com.roottherapy.backend.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request used by an authenticated account to replace its password.
 */
public record ChangePasswordRequest(
        @NotBlank
        @Size(min = 8, max = 72)
        String currentPassword,

        @NotBlank
        @Size(min = 8, max = 72)
        String newPassword,

        @NotBlank
        @Size(min = 8, max = 72)
        String confirmNewPassword
) {
}
