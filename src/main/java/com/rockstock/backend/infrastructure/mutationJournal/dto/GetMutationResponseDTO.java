package com.rockstock.backend.infrastructure.mutationJournal.dto;

import com.rockstock.backend.entity.stock.MutationJournal;
import com.rockstock.backend.entity.stock.MutationStatus;
import com.rockstock.backend.entity.stock.StockAdjustmentType;
import com.rockstock.backend.entity.stock.StockChangeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetMutationResponseDTO {
        private Long productId;
        private String productName;
        private String originWarehouse;
        private String destinationWarehouse;
        private Long mutationQuantity;
        private Long previousStockQuantity;
        private Long newStockQuantity;
        private StockAdjustmentType stockAdjustmentType;
        private StockChangeType stockChangeType;
        private MutationStatus mutationStatus;
        private String description;
        private OffsetDateTime updatedAt;

        public static GetMutationResponseDTO fromMutationJournal(MutationJournal journal) {
                return new GetMutationResponseDTO(
                        journal.getWarehouseStock().getProduct().getId(),
                        journal.getWarehouseStock().getProduct().getProductName(),
                        journal.getOriginWarehouse() != null ? journal.getOriginWarehouse().getName() : "N/A",
                        journal.getDestinationWarehouse() != null ? journal.getDestinationWarehouse().getName() : "N/A",
                        journal.getMutationQuantity(),
                        journal.getPreviousStockQuantity(),
                        journal.getNewStockQuantity(),
                        journal.getStockAdjustmentType(),
                        journal.getStockChangeType(),
                        journal.getMutationStatus(),
                        journal.getDescription(),
                        journal.getUpdatedAt()
                );
        }
}