package com.rockstock.backend.infrastructure.user.dto;

import com.rockstock.backend.entity.user.User;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetAllUsersDTO {
    private Long id;
    private String fullname;
    private String email;
    private String photoProfileUrl;
    private String googleImageUrl;
    private OffsetDateTime birthDate;
    private String gender;
    private Boolean isVerified;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public static GetAllUsersDTO fromEntity(User user) {
        return GetAllUsersDTO.builder()
                .id(user.getId())
                .fullname(user.getFullname())
                .email(user.getEmail())
                .photoProfileUrl(user.getPhotoProfileUrl())
                .googleImageUrl(user.getGoogleImageUrl())
                .birthDate(user.getBirthDate())
                .gender(user.getGender())
                .isVerified(user.getIsVerified())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
