package com.rockstock.backend.infrastructure.warehouseStock.specification;

import com.rockstock.backend.entity.stock.WarehouseStock;
import org.springframework.data.jpa.domain.Specification;

public class FilterWarehouseStockSpecification {

    public static Specification<WarehouseStock> hasProductName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.isEmpty()) {
                return criteriaBuilder.conjunction(); // No filter if name is null or empty
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("product").get("productName")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<WarehouseStock> hasWarehouseId(Long warehouseId) {
        return (root, query, criteriaBuilder) -> {
            if (warehouseId == null) {
                return criteriaBuilder.conjunction(); // No filter if warehouseId is null
            }
            return criteriaBuilder.equal(root.get("warehouse").get("id"), warehouseId);
        };
    }

    public static Specification<WarehouseStock> isDeletedAtNull() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isNull(root.get("deletedAt"));
    }

    public static Specification<WarehouseStock> withFilters(String productName, Long warehouseId) {
        return Specification.where(isDeletedAtNull())
                .and(hasProductName(productName))
                .and(hasWarehouseId(warehouseId));
    }
}