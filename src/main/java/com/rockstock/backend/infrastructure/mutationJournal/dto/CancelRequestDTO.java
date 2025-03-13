package com.rockstock.backend.infrastructure.mutationJournal.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancelRequestDTO {
    @Size(min = 3, max = 100)
    private String description;
}
