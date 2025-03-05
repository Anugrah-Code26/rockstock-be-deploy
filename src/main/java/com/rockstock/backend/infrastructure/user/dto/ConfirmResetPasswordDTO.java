package com.rockstock.backend.infrastructure.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ConfirmResetPasswordDTO {
    @NotBlank
    private String resetToken;

    @NotBlank
    @Size(min = 8, message = "Old password must be at least 8 characters")
    private String oldPassword;

    @NotBlank
    @Size(min = 8, message = "New password must be at least 8 characters")
    private String newPassword;

    @NotBlank
    private String confirmationNewPassword;

    // Getters
    public String getResetToken() {
        return resetToken;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public String getConfirmationNewPassword() {
        return confirmationNewPassword;
    }
}
