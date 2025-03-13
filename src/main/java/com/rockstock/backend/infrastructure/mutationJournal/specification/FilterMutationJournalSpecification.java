package com.rockstock.backend.infrastructure.mutationJournal.specification;

import com.rockstock.backend.entity.stock.MutationJournal;
import com.rockstock.backend.entity.stock.MutationStatus;
import com.rockstock.backend.entity.stock.StockAdjustmentType;
import com.rockstock.backend.entity.stock.StockChangeType;
import org.springframework.data.jpa.domain.Specification;

public class FilterMutationJournalSpecification {

    public static Specification<MutationJournal> hasProductName(String productName) {
        return (root, query, criteriaBuilder) -> {
            if (productName == null || productName.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("warehouseStock").get("product").get("productName")),
                    "%" + productName.toLowerCase() + "%");
        };
    }

    public static Specification<MutationJournal> hasDestinationWarehouseId(Long warehouseId) {
        return (root, query, criteriaBuilder) -> {
            if (warehouseId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("destinationWarehouse").get("id"), warehouseId);
        };
    }

    public static Specification<MutationJournal> hasMutationStatus(MutationStatus status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("mutationStatus"), status);
        };
    }

    public static Specification<MutationJournal> hasStockAdjustmentType(StockAdjustmentType adjustmentType) {
        return (root, query, criteriaBuilder) -> {
            if (adjustmentType == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("stockAdjustmentType"), adjustmentType);
        };
    }

    public static Specification<MutationJournal> hasStockChangeType(StockChangeType stockChangeType) {
        return (root, query, criteriaBuilder) -> {
            if (stockChangeType == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("stockChangeType"), stockChangeType);
        };
    }

    public static Specification<MutationJournal> withFilters(
            String productName, Long warehouseId, MutationStatus status,
            StockAdjustmentType adjustmentType, StockChangeType stockChangeType) {

        return Specification.where(hasProductName(productName))
                .and(hasDestinationWarehouseId(warehouseId))
                .and(hasMutationStatus(status))
                .and(hasStockAdjustmentType(adjustmentType))
                .and(hasStockChangeType(stockChangeType));
    }
}