package com.rockstock.backend.infrastructure.admin.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminCreateRequestDTO {
    private String email;
    private String password;
    private String fullname;
}
