package com.rockstock.backend.service.user.auth.impl;

import com.rockstock.backend.infrastructure.user.auth.dto.TokenPairResponseDTO;
import com.rockstock.backend.service.user.auth.TokenGenerationService;
import com.rockstock.backend.service.user.auth.TokenRefreshService;
import org.springframework.stereotype.Service;

@Service
public class TokenRefreshServiceImpl implements TokenRefreshService {
    private final TokenGenerationService tokenService;

    public TokenRefreshServiceImpl(TokenGenerationService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public TokenPairResponseDTO refreshAccessToken(String refreshToken) {
        String newAccessToken = tokenService.refreshAccessToken(refreshToken);
        return new TokenPairResponseDTO(newAccessToken, refreshToken, "Bearer","");
    }
}
