package com.rockstock.backend.infrastructure.user.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuthLoginRequest {
    private String idToken;  // ID token received from Google OAuth
    private String accessToken;  // Access token received from Google OAuth

    public OAuthLoginRequest(String idToken, String accessToken) {
        this.idToken = idToken;
        this.accessToken = accessToken;
    }
}
