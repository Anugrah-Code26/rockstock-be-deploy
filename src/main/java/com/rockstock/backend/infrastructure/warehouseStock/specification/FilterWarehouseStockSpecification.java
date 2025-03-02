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

    public static Specification<WarehouseStock> hasWarehouseName(String warehouseName) {
        return (root, query, criteriaBuilder) -> {
            if (warehouseName == null || warehouseName.isEmpty()) {
                return criteriaBuilder.conjunction(); // No filter if warehouseName is null or empty
            }
            return criteriaBuilder.equal(criteriaBuilder.lower(root.get("warehouse").get("name")), warehouseName.toLowerCase());
        };
    }

    public static Specification<WarehouseStock> isDeletedAtNull() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isNull(root.get("deletedAt"));
    }

    public static Specification<WarehouseStock> withFilters(String productName, String warehouseName) {
        return Specification.where(isDeletedAtNull())
                .and(hasProductName(productName))
                .and(hasWarehouseName(warehouseName));
    }
}