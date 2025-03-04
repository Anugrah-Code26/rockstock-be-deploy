package com.rockstock.backend.infrastructure.user.auth.controller;

import com.rockstock.backend.infrastructure.user.auth.dto.OAuthLoginRequest;
import com.rockstock.backend.infrastructure.user.auth.dto.OAuthLoginResponse;
import com.rockstock.backend.service.user.auth.OAuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j // ✅ Gunakan Lombok untuk logging
@RestController
@RequestMapping("/api/v1/auth/oauth")
public class OAuthController {
    private final OAuthService oAuthService;

    @Autowired
    public OAuthController(OAuthService oAuthService) {
        this.oAuthService = oAuthService;
    }

    @PostMapping("/google")
    public ResponseEntity<?> handleGoogleLogin(@Valid @RequestBody OAuthLoginRequest googleAuthRequest) {
        log.info("Received OAuth login request: {}", googleAuthRequest);

        if (googleAuthRequest.getIdToken() == null || googleAuthRequest.getAccessToken() == null) {
            log.warn("OAuth Login Failed: Missing idToken or accessToken");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing tokens");
        }

        try {
            OAuthLoginResponse response = oAuthService.processGoogleLogin(
                    googleAuthRequest.getIdToken(),
                    googleAuthRequest.getAccessToken()
            );

            log.info("OAuth login successful for user: {}", response.getFullname()); // ✅ Pastikan fullname digunakan
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("OAuth Login Failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("OAuth Login Failed: " + e.getMessage());
        }
    }
}
