package com.rockstock.backend.infrastructure.mutationJournal.dto;

import com.rockstock.backend.entity.stock.MutationStatus;
import com.rockstock.backend.entity.stock.StockChangeType;
import lombok.*;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetMutationResponseDTO {
        private Long journalId;
        private Long productId;
        private String productName;
        private String originWarehouse;
        private String destinationWarehouse;
        private Long mutationQuantity;
        private Long previousStockQuantity;
        private Long newStockQuantity;
        private StockChangeType stockChangeType;
        private MutationStatus mutationStatus;
        private String description;
        private OffsetDateTime updatedAt;
}
