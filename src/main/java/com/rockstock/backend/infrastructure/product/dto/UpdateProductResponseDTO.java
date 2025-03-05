package com.rockstock.backend.infrastructure.product.dto;

import com.rockstock.backend.entity.product.Product;
import com.rockstock.backend.entity.product.ProductStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductResponseDTO {
    private Long productId;
    private String productName;
    private String detail;
    private BigDecimal price;
    private BigDecimal weight;
    private String categoryName;
    private ProductStatus status;
    private OffsetDateTime updatedAt;

    public static UpdateProductResponseDTO fromProduct(Product product) {
        return new UpdateProductResponseDTO(
                product.getId(),
                product.getProductName(),
                product.getDetail(),
                product.getPrice(),
                product.getWeight(),
                product.getProductCategory() != null ? product.getProductCategory().getCategoryName() : null,
                product.getStatus(),
                product.getUpdatedAt()
        );
    }
}