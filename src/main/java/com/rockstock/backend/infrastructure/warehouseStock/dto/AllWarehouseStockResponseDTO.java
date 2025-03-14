package com.rockstock.backend.infrastructure.warehouseStock.dto;

import com.rockstock.backend.entity.product.ProductPicture;
import com.rockstock.backend.entity.stock.WarehouseStock;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AllWarehouseStockResponseDTO {
    private Long stockId;
    private Long stockQuantity;
    private Long lockedQuantity;
    private String productName;
    private Long productId;
    private String warehouseName;
    private Long warehouseId;
    private String productPictureUrl;

    @Getter(AccessLevel.NONE)
    private Long availableQuantity;

    public Long getAvailableQuantity() {
        long stock = (stockQuantity != null ? stockQuantity : 0L);
        long locked = (lockedQuantity != null ? lockedQuantity : 0L);
        return stock - locked;
    }

    public static AllWarehouseStockResponseDTO fromWarehouseStock(WarehouseStock stock) {
        return AllWarehouseStockResponseDTO.builder()
                .stockId(stock.getId())
                .stockQuantity(stock.getStockQuantity())
                .lockedQuantity(stock.getLockedQuantity())
                .productName(stock.getProduct().getProductName())
                .productId(stock.getProduct().getId())
                .warehouseName(stock.getWarehouse().getName())
                .warehouseId(stock.getWarehouse().getId())
                .productPictureUrl(
                        stock.getProduct().getProductPictures().stream()
                                .filter(pic -> pic.getPosition() == 1)
                                .map(ProductPicture::getProductPictureUrl)
                                .findFirst()
                                .orElse(null)
                )
                .build();
    }
}