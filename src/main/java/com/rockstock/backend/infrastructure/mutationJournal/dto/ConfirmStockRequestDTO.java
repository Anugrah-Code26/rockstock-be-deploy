package com.rockstock.backend.infrastructure.mutationJournal.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmStockRequestDTO {
    @NotNull(message = "Status must be provided")
    private boolean completed;

    private String description;
}
