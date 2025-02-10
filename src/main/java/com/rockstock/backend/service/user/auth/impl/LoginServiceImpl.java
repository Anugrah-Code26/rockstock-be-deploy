package com.rockstock.backend.service.user.auth.impl;

import com.rockstock.backend.common.exceptions.DataNotFoundException;
import com.rockstock.backend.infrastructure.user.auth.dto.LoginRequestDTO;
import com.rockstock.backend.infrastructure.user.auth.dto.TokenPairResponseDTO;
import com.rockstock.backend.service.user.auth.LoginService;
import com.rockstock.backend.service.user.auth.TokenGenerationService;

import lombok.extern.java.Log;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Log
@Service
public class LoginServiceImpl implements LoginService {
    private final long ACCESS_TOKEN_EXPIRY = 900L;
    private final long REFRESH_TOKEN_EXPIRY = 86400L;

    private final AuthenticationManager authenticationManager;
    private final TokenGenerationService tokenService;

    public LoginServiceImpl(AuthenticationManager authenticationManager, TokenGenerationService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @Override
    public TokenPairResponseDTO authenticateUser(LoginRequestDTO req) {
        try {
            log.info("Login with");
            log.info(req.getEmail());
            log.info(req.getPassword());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
            );
            String accessToken = tokenService.generateToken(authentication, TokenGenerationService.TokenType.ACCESS);
            String refreshToken = tokenService.generateToken(authentication, TokenGenerationService.TokenType.REFRESH);
            return new TokenPairResponseDTO(accessToken, refreshToken, "Bearer");
        } catch (AuthenticationException e) {
            throw new DataNotFoundException("Wrong credentials");
        }
    }
}