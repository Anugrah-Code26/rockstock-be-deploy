package com.rockstock.backend.infrastructure.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AdminResponseDTO {
    private Long id;
    private String email;
    private String role;
    private String fullname;
}
