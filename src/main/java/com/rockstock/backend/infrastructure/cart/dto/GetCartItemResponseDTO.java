package com.rockstock.backend.infrastructure.cart.dto;

import com.rockstock.backend.entity.cart.CartItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetCartItemResponseDTO {

    private Long cartItemId;
    private BigDecimal quantity;
    private BigDecimal totalAmount;
    private Long cartId;
    private Long productId;

    public GetCartItemResponseDTO(CartItem cartItem) {
        this.cartItemId = cartItem.getId();
        this.quantity = cartItem.getQuantity();
        this.totalAmount = cartItem.getTotalAmount();
        this.cartId = cartItem.getCart().getId();
        this.productId = cartItem.getProduct().getId();
    }
}
