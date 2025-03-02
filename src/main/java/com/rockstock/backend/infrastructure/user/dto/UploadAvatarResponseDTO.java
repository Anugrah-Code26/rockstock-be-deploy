package com.rockstock.backend.infrastructure.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UploadAvatarResponseDTO {
    @JsonProperty("photo_profile_url")
    private String photoProfileUrl;
}
