package com.rockstock.backend.infrastructure.cart.dto;

import com.rockstock.backend.entity.cart.CartItem;
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

    public GetCartItemResponseDTO(CartItem cartItem) {
        this.cartItemId = cartItem.getId();
        this.quantity = cartItem.getQuantity();
        this.totalAmount = cartItem.getTotalAmount();
        this.deletedAt = cartItem.getDeletedAt();
        this.cartId = cartItem.getCart().getId();
        this.productId = cartItem.getProduct().getId();
        this.productName = cartItem.getProduct().getProductName();
        this.productPrice = cartItem.getProduct().getPrice();
    }
}
