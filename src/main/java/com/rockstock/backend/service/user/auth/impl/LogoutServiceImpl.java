package com.rockstock.backend.service.user.auth.impl;

import com.rockstock.backend.infrastructure.user.auth.dto.LogoutRequestDTO;
import com.rockstock.backend.service.user.auth.LogoutService;
import com.rockstock.backend.service.user.auth.TokenBlacklistService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class LogoutServiceImpl implements LogoutService {
    private final JwtDecoder jwtDecoder;
    private final TokenBlacklistService tokenBlacklistService;


    public LogoutServiceImpl(
            JwtDecoder jwtDecoder,
            TokenBlacklistService tokenBlacklistService
    ) {
        this.jwtDecoder = jwtDecoder;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    public Boolean logoutUser(LogoutRequestDTO req) {
        Jwt accessToken = jwtDecoder.decode(req.getAccessToken());
        Jwt refreshToken = jwtDecoder.decode(req.getRefreshToken());

        tokenBlacklistService.blacklistToken(accessToken.getTokenValue(), Objects.requireNonNull(accessToken.getExpiresAt()).toString());
        tokenBlacklistService.blacklistToken(refreshToken.getTokenValue(), Objects.requireNonNull(refreshToken.getExpiresAt()).toString());
        return Boolean.TRUE;
    }
}
