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
public class GetAllMutationResponseDTO {
        private Long journalId;
        private String productName;
        private String originWarehouse;
        private String destinationWarehouse;
        private Long mutationQuantity;
        private StockAdjustmentType stockAdjustmentType;
        private StockChangeType stockChangeType;
        private MutationStatus mutationStatus;
        private OffsetDateTime updatedAt;

        public static GetAllMutationResponseDTO fromMutationJournal(MutationJournal journal) {
                return new GetAllMutationResponseDTO(
                        journal.getId(),
                        journal.getWarehouseStock().getProduct().getProductName(),
                        journal.getOriginWarehouse() != null ? journal.getOriginWarehouse().getName() : "N/A",
                        journal.getDestinationWarehouse() != null ? journal.getDestinationWarehouse().getName() : "N/A",
                        journal.getMutationQuantity(),
                        journal.getStockAdjustmentType(),
                        journal.getStockChangeType(),
                        journal.getMutationStatus(),
                        journal.getUpdatedAt()
                );
        }
}