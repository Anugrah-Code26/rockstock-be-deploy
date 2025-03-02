package com.rockstock.backend.infrastructure.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {
    private Long id;

    @NotNull(message = "Fullname is mandatory")
    @Size(max = 100, message = "Fullname can have at most 100 characters")
    private String fullname;

    @NotNull(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;

    private String phoneNumber;
    private String avatarUrl;
    private OffsetDateTime birthDate;

    @Size(max = 10, message = "Gender can have at most 10 characters")
    private String gender;

    @NotNull
    private Boolean emailVerified;

    public UserProfileResponse(Long id, @NotNull(message = "Fullname is mandatory") @Size(max = 100, message = "Fullname can have at most 100 characters") String fullname, @NotNull(message = "Email is mandatory") @Email(message = "Email should be valid") String email, String photoProfileUrl, OffsetDateTime birthDate, @Size(max = 10, message = "Gender can have at most 10 characters") String gender, @NotNull Boolean isVerified) {
    }
}
