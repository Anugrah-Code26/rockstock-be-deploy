package com.rockstock.backend.service.order.impl;

import com.rockstock.backend.common.exceptions.DataNotFoundException;
import com.rockstock.backend.common.exceptions.UnauthorizedException;
import com.rockstock.backend.entity.cart.Cart;
import com.rockstock.backend.entity.cart.CartItem;
import com.rockstock.backend.entity.order.Order;
import com.rockstock.backend.entity.order.OrderItem;
import com.rockstock.backend.entity.product.Product;
import com.rockstock.backend.infrastructure.cart.repository.CartItemRepository;
import com.rockstock.backend.infrastructure.cart.repository.CartRepository;
import com.rockstock.backend.infrastructure.order.repository.OrderItemRepository;
import com.rockstock.backend.infrastructure.order.repository.OrderRepository;
import com.rockstock.backend.infrastructure.product.repository.ProductRepository;
import com.rockstock.backend.infrastructure.user.auth.security.Claims;
import com.rockstock.backend.service.order.CreateOrderItemService;
import com.rockstock.backend.common.exceptions.DuplicateDataException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CreateOrderItemServiceImpl implements CreateOrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    @Transactional
    public void createOrderItem(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new DataNotFoundException("Order not found"));

        Long userId = Claims.getUserIdFromJwt();

        if (!order.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("Unauthorized: Order does not belong to the user");
        }

        Cart cart = cartRepository.findActiveCartByUserId(userId);
        if (cart == null) {
            throw new DataNotFoundException("Cart not found ");
        }

        List<CartItem> cartItems = cartItemRepository.findAllByActiveCartId(cart.getId());
        if (cartItems.isEmpty()) {
            throw new DataNotFoundException("User still not have any items in the cart !");
        }

        List<OrderItem> orderItems = cartItems.stream().map(cartItem -> {
            Product product = productRepository.findById(cartItem.getProduct().getId())
                    .orElseThrow(() -> new DataNotFoundException("Product not found"));

            Optional<OrderItem> checkOrderItem = orderItemRepository.findByOrderIdAndProductId(order.getId(), product.getId());
            if (checkOrderItem.isPresent()) {
                throw new DuplicateDataException("Order item is already exist !");
            }

            OrderItem orderItem = new OrderItem();

            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItem.setOrder(order);
            orderItem.setProduct(product);

            return orderItem;
        }).toList();

        orderItemRepository.saveAll(orderItems);
    }
}
