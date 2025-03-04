package com.rockstock.backend.infrastructure.mutationJournal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessRequestDTO {
    @NotNull(message = "Approval status must be provided")
    private boolean approved;

    private String description;
}