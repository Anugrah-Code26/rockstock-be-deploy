package com.rockstock.backend.infrastructure.user.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuthUserInfo {
    private String fullname; // Fix: Sesuai database
    private String email;
    private String profilePictureUrl;

    public OAuthUserInfo(String fullname, String email, String profilePictureUrl) {
        this.fullname = fullname; // Fix: Sesuai dengan database
        this.email = email;
        this.profilePictureUrl = profilePictureUrl;
    }
}
