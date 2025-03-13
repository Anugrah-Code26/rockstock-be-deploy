package com.rockstock.backend.infrastructure.product.specification;

import com.rockstock.backend.entity.product.Product;
import com.rockstock.backend.entity.product.ProductStatus;
import org.springframework.data.jpa.domain.Specification;

public class FilterProductSpecifications {
    public static Specification<Product> hasProductName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("productName")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<Product> hasCategoryId(Long categoryId) {
        return (root, query, criteriaBuilder) -> {
            if (categoryId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("productCategory").get("id"), categoryId);
        };
    }

    public static Specification<Product> hasStatus(ProductStatus... statuses) {
        return (root, query, criteriaBuilder) -> {
            if (statuses == null || statuses.length == 0) {
                return criteriaBuilder.conjunction();
            }
            return root.get("status").in((Object[]) statuses);
        };
    }
}