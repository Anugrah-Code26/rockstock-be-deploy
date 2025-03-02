package com.rockstock.backend.infrastructure.user.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuthLoginResponse {
    private String accessToken;  // JWT access token
    private String refreshToken;  // JWT refresh token
    private String fullname;  // Menggunakan fullname sesuai database
    private String email;  // User's email from OAuth
    private String profilePictureUrl;  // User's profile picture URL from OAuth

    public OAuthLoginResponse(String accessToken, String refreshToken, String fullname, String email, String profilePictureUrl) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.fullname = fullname; // Fix: Sesuai dengan database
        this.email = email;
        this.profilePictureUrl = profilePictureUrl;
    }
}
