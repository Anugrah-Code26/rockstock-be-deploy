package com.rockstock.backend.infrastructure.order.dto;

import com.rockstock.backend.entity.order.OrderItem;
import com.rockstock.backend.infrastructure.productPicture.dto.GetProductPicturesResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetOrderItemResponseDTO {

    private Long orderItemId;
    private Integer quantity;
    private BigDecimal price;
    private Long orderId;
    private Long productId;
    private String productName;
    private String productDetail;
    private BigDecimal productWeight;
    private GetProductPicturesResponseDTO productPicture;

    public GetOrderItemResponseDTO(OrderItem orderItem) {
        this.orderItemId = orderItem.getId();
        this.quantity = orderItem.getQuantity();
        this.price = orderItem.getPrice();
        this.orderId = orderItem.getOrder().getId();
        this.productId = orderItem.getProduct().getId();
        this.productName = orderItem.getProduct().getProductName();
        this.productDetail = orderItem.getProduct().getDetail();
        this.productWeight = orderItem.getProduct().getWeight();
        this.productPicture = orderItem.getProduct().getProductPictures().stream()
                .filter(picture -> picture.getPosition() == 1)
                .findFirst()
                .map(GetProductPicturesResponseDTO::fromProductPicture)
                .orElse(null);
    }
}
