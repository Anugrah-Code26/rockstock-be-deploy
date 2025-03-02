package com.rockstock.backend.service.user.auth;

import com.rockstock.backend.infrastructure.user.auth.dto.SetupPasswordRequestDTO;
import jakarta.validation.Valid;

public interface EmailVerificationService {
    void sendVerificationEmail(String email);
    void verifyEmail(String verificationToken);
    void setupPassword(String token, @Valid SetupPasswordRequestDTO req);
    void resendVerificationEmail(String email);

}
