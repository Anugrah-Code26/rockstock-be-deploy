package com.rockstock.backend.infrastructure.product.dto;

import com.rockstock.backend.entity.product.Product;
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
public class EditProductResponseDTO {
    private Long productId;
    private String productName;
    private String detail;
    private BigDecimal price;
    private BigDecimal weight;
    private String categoryName;
    private OffsetDateTime updatedAt;

    public static EditProductResponseDTO fromProduct(Product product) {
        return new EditProductResponseDTO(
                product.getId(),
                product.getProductName(),
                product.getDetail(),
                product.getPrice(),
                product.getWeight(),
                product.getProductCategory() != null ? product.getProductCategory().getCategoryName() : null,
                product.getUpdatedAt()
        );
    }
}
