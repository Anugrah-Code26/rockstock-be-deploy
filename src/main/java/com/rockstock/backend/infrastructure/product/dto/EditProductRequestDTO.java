package com.rockstock.backend.infrastructure.product.dto;

import com.rockstock.backend.entity.product.Product;
import com.rockstock.backend.entity.product.ProductCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EditProductRequestDTO {
    @NotBlank
    @Size(min = 3, max = 30, message = "Product name must be between 3 and 30 characters long")
    private String productName;

    @NotBlank
    @Size(min = 3, max = 100, message = "Details must be between 3 and 100 characters long")
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