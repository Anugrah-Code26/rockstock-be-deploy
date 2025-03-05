package com.rockstock.backend.infrastructure.product.dto;

import com.rockstock.backend.entity.product.Product;
import com.rockstock.backend.entity.product.ProductCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductRequestDTO {
    @NotBlank
    private String productName;

    @NotBlank
    private String detail;

    @NotNull
    private BigDecimal price;

    @NotNull
    private BigDecimal weight;

    private Long categoryId;

    public Product toProduct(ProductCategory category) {
        Product product = new Product();
        product.setProductName(this.productName);
        product.setDetail(this.detail);
        product.setPrice(this.price);
        product.setWeight(this.weight);
        product.setProductCategory(category);
        product.setTotalStock(BigDecimal.ZERO);
        product.setCreatedAt(OffsetDateTime.now());
        product.setUpdatedAt(OffsetDateTime.now());
        return product;
    }
}