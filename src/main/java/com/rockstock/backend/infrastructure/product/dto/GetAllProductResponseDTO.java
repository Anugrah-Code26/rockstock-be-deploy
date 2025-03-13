package com.rockstock.backend.infrastructure.product.dto;

import com.rockstock.backend.entity.product.Product;
import com.rockstock.backend.entity.product.ProductPicture;
import com.rockstock.backend.infrastructure.productPicture.dto.GetProductPicturesResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllProductResponseDTO {
    private Long productId;
    private String productName;
    private String detail;
    private BigDecimal price;
    private BigDecimal weight;
    private BigDecimal totalStock;
    private Long categoryId;
    private String categoryName;
    private List<GetProductPicturesResponseDTO> productPictures;

    public static GetAllProductResponseDTO fromProduct(Product product, BigDecimal totalStock) {
        return new GetAllProductResponseDTO(
                product.getId(),
                product.getProductName(),
                product.getDetail(),
                product.getPrice(),
                product.getWeight(),
                totalStock,
                product.getProductCategory().getId(),
                product.getProductCategory().getCategoryName(),
                product.getProductPictures().stream()
                        .sorted(Comparator.comparingInt(ProductPicture::getPosition)) // ✅ Sort by position
                        .limit(3) // ✅ Take only the first 3 pictures
                        .map(GetProductPicturesResponseDTO::fromProductPicture)
                        .collect(Collectors.toList())
        );
    }
}