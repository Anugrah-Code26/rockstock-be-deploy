package com.rockstock.backend.infrastructure.warehouseStock.dto;

import com.rockstock.backend.entity.product.ProductPicture;
import com.rockstock.backend.entity.stock.WarehouseStock;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WarehouseStockResponseDTO {
    private Long stockId;
    private Long stockQuantity;
    private Long lockedQuantity;
    private String productName;
    private Long productId;
    private String warehouseName;
    private Long warehouseId;
    private List<String> productPictureUrls;

    @Getter(AccessLevel.NONE)
    private Long availableQuantity;

    public Long getAvailableQuantity() {
        long stock = (stockQuantity != null ? stockQuantity : 0L);
        long locked = (lockedQuantity != null ? lockedQuantity : 0L);
        return stock - locked;
    }

    public static WarehouseStockResponseDTO fromWarehouseStock(WarehouseStock stock) {
        return WarehouseStockResponseDTO.builder()
                .stockId(stock.getId())
                .stockQuantity(stock.getStockQuantity())
                .lockedQuantity(stock.getLockedQuantity())
                .productName(stock.getProduct().getProductName())
                .productId(stock.getProduct().getId())
                .warehouseName(stock.getWarehouse().getName())
                .warehouseId(stock.getWarehouse().getId())
                .productPictureUrls(
                        stock.getProduct().getProductPictures().stream()
                                .map(ProductPicture::getProductPictureUrl)
                                .collect(Collectors.toList())
                )
                .build();
    }
}