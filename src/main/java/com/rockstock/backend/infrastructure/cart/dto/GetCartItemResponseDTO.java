package com.rockstock.backend.infrastructure.cart.dto;

import com.rockstock.backend.entity.cart.CartItem;
import com.rockstock.backend.infrastructure.productPicture.dto.GetProductPicturesResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetCartItemResponseDTO {

    private Long cartItemId;
    private Integer quantity;
    private BigDecimal totalAmount;
    private OffsetDateTime deletedAt;
    private Long cartId;
    private Long productId;
    private String productName;
    private BigDecimal productPrice;
    private BigDecimal productWeight;
    private GetProductPicturesResponseDTO productPictures;

    public GetCartItemResponseDTO(CartItem cartItem) {
        this.cartItemId = cartItem.getId();
        this.quantity = cartItem.getQuantity();
        this.totalAmount = cartItem.getTotalAmount();
        this.deletedAt = cartItem.getDeletedAt();
        this.cartId = cartItem.getCart().getId();
        this.productId = cartItem.getProduct().getId();
        this.productName = cartItem.getProduct().getProductName();
        this.productPrice = cartItem.getProduct().getPrice();
        this.productWeight = cartItem.getProduct().getWeight();
        this.productPictures = cartItem.getProduct().getProductPictures().stream()
                .filter(picture -> picture.getPosition() == 1)
                .findFirst()
                .map(GetProductPicturesResponseDTO::fromProductPicture)
                .orElse(null);
    }
}
