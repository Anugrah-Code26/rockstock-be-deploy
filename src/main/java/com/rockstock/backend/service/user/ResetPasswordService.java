package com.rockstock.backend.service.user;

import com.rockstock.backend.infrastructure.user.dto.ConfirmResetPasswordDTO;
import com.rockstock.backend.infrastructure.user.dto.ResetPasswordRequestDTO;

public interface ResetPasswordService {
    void sendResetPasswordEmail(ResetPasswordRequestDTO request);
    void confirmResetPassword(ConfirmResetPasswordDTO request);
}
