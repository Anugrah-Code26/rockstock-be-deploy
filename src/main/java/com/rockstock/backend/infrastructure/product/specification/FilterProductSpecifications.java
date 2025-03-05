package com.rockstock.backend.infrastructure.product.specification;

import com.rockstock.backend.entity.product.Product;
import com.rockstock.backend.entity.product.ProductStatus;
import org.springframework.data.jpa.domain.Specification;

public class FilterProductSpecifications {
    public static Specification<Product> hasProductName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.isEmpty()) {
                return criteriaBuilder.conjunction(); // No filter if name is null or empty
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("productName")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<Product> hasCategoryName(String category) {
        return (root, query, criteriaBuilder) -> {
            if (category == null || category.isEmpty()) {
                return criteriaBuilder.conjunction(); // No filter if category is null or empty
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("productCategory").get("categoryName")), "%" + category.toLowerCase() + "%");
        };
    }

    public static Specification<Product> hasStatus(ProductStatus status) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status);
    }
}