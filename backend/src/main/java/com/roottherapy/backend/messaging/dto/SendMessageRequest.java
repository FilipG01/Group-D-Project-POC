package com.roottherapy.backend.messaging.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SendMessageRequest(

        @NotBlank
        @Size(max = 10000)
        String ciphertext,

        @NotBlank
        @Size(max = 100)
        String encryptionAlgorithm,

        @NotBlank
        @Size(max = 1000)
        String iv,

        @Size(max = 1000)
        String authTag

) {
}
