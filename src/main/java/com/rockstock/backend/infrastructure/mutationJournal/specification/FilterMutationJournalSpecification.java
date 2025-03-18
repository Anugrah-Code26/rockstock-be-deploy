package com.rockstock.backend.infrastructure.mutationJournal.specification;

import com.rockstock.backend.entity.stock.MutationJournal;
import com.rockstock.backend.entity.stock.MutationStatus;
import com.rockstock.backend.entity.stock.StockAdjustmentType;
import com.rockstock.backend.entity.stock.StockChangeType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class FilterMutationJournalSpecification {

    public static Specification<MutationJournal> hasProductName(String productName) {
        return (root, query, criteriaBuilder) -> {
            if (productName == null || productName.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("warehouseStock").get("product").get("productName")),
                    "%" + productName.toLowerCase() + "%"
            );
        };
    }

    public static Specification<MutationJournal> hasWarehouseId(Long warehouseId) {
        return (root, query, criteriaBuilder) -> {
            if (warehouseId == null) {
                return criteriaBuilder.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();

            // TRANSFER & NEGATIVE or SALES_DISPATCHED & NEGATIVE → originWarehouseId
            predicates.add(criteriaBuilder.and(
                    criteriaBuilder.or(
                            criteriaBuilder.equal(root.get("stockChangeType"), StockChangeType.TRANSFER),
                            criteriaBuilder.equal(root.get("stockChangeType"), StockChangeType.SALES_DISPATCHED)
                    ),
                    criteriaBuilder.equal(root.get("stockAdjustmentType"), StockAdjustmentType.NEGATIVE),
                    criteriaBuilder.equal(root.get("originWarehouse").get("id"), warehouseId)
            ));

            // TRANSFER & POSITIVE → destinationWarehouseId
            predicates.add(criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("stockChangeType"), StockChangeType.TRANSFER),
                    criteriaBuilder.equal(root.get("stockAdjustmentType"), StockAdjustmentType.POSITIVE),
                    criteriaBuilder.equal(root.get("destinationWarehouse").get("id"), warehouseId)
            ));

            // STOCK_ADJUSTMENT → destinationWarehouseId
            predicates.add(criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("stockChangeType"), StockChangeType.STOCK_ADJUSTMENT),
                    criteriaBuilder.equal(root.get("destinationWarehouse").get("id"), warehouseId)
            ));

            // PENDING & POSITIVE → originWarehouseId
            predicates.add(criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("mutationStatus"), MutationStatus.PENDING),
                    criteriaBuilder.equal(root.get("stockAdjustmentType"), StockAdjustmentType.POSITIVE),
                    criteriaBuilder.equal(root.get("originWarehouse").get("id"), warehouseId)
            ));

            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
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
            String productName, Long warehouseId, String status,
            String adjustmentType, String stockChangeType) {

        MutationStatus mutationStatus = parseEnum(status, MutationStatus.class);
        StockAdjustmentType adjType = parseEnum(adjustmentType, StockAdjustmentType.class);
        StockChangeType changeType = parseEnum(stockChangeType, StockChangeType.class);

        return Specification.where(hasProductName(productName))
                .and(hasWarehouseId(warehouseId))
                .and(hasMutationStatus(mutationStatus))
                .and(hasStockAdjustmentType(adjType))
                .and(hasStockChangeType(changeType));
    }

    private static <E extends Enum<E>> E parseEnum(String value, Class<E> enumClass) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Enum.valueOf(enumClass, value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}