package com.rockstock.backend.infrastructure.admin.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUpdateRequestDTO {
    private String email;
    private String password;
    private String role;
    private String fullname;
}
