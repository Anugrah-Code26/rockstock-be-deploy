package com.rockstock.backend.infrastructure.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPublicDetailsDTO {
    private Long userId;
    private String photoProfileUrl;
    private String fullName;
    private String email;
}
