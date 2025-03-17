package com.rockstock.backend.infrastructure.mutationJournal.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MutationRequestDTO {
    @NotNull
    private Long originWarehouseId;

    @NotNull(message = "Quantity cannot be null")
    private Long mutationQuantity;

    @Size(min = 3, max = 100)
    @Column(length = 100)
    private String description;
}   