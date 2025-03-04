package com.rockstock.backend.service.user.auth;

import com.rockstock.backend.infrastructure.user.auth.dto.OAuthLoginResponse;

public interface OAuthService {
    OAuthLoginResponse processGoogleLogin(String idToken, String accessToken);
}
