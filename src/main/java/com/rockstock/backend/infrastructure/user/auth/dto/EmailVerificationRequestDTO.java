package com.rockstock.backend.infrastructure.user.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailVerificationRequestDTO {
    @NotBlank(message = "Verification token is required.")
    private String token;
}
