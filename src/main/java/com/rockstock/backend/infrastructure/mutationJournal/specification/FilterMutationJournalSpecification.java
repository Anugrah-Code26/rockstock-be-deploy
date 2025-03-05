package com.rockstock.backend.infrastructure.mutationJournal.specification;

import com.rockstock.backend.entity.stock.MutationJournal;
import com.rockstock.backend.entity.stock.MutationStatus;
import org.springframework.data.jpa.domain.Specification;

public class FilterMutationJournalSpecification {

    public static Specification<MutationJournal> hasProductId(Long productId) {
        return (root, query, criteriaBuilder) -> {
            if (productId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("warehouseStock").get("product").get("id"), productId);
        };
    }

    public static Specification<MutationJournal> hasOriginWarehouseName(String warehouseName) {
        return (root, query, criteriaBuilder) -> {
            if (warehouseName == null || warehouseName.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(criteriaBuilder.lower(root.get("originWarehouse").get("name")), warehouseName.toLowerCase());
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

    public static Specification<MutationJournal> withFilters(Long productId, String warehouseName, MutationStatus status) {
        return Specification.where(hasProductId(productId))
                .and(hasOriginWarehouseName(warehouseName))
                .and(hasMutationStatus(status));
    }
}