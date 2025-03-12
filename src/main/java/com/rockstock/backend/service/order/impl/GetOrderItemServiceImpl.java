package com.rockstock.backend.service.order.impl;

import com.rockstock.backend.common.exceptions.DataNotFoundException;
import com.rockstock.backend.common.exceptions.UnauthorizedException;
import com.rockstock.backend.entity.order.Order;
import com.rockstock.backend.entity.order.OrderItem;
import com.rockstock.backend.infrastructure.order.dto.GetOrderItemResponseDTO;
import com.rockstock.backend.infrastructure.order.dto.GetOrderResponseDTO;
import com.rockstock.backend.infrastructure.order.repository.OrderItemRepository;
import com.rockstock.backend.infrastructure.order.repository.OrderRepository;
import com.rockstock.backend.infrastructure.user.auth.security.Claims;
import com.rockstock.backend.service.order.GetOrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetOrderItemServiceImpl implements GetOrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;

    @Override
    public List<GetOrderItemResponseDTO> getFilteredOrderItems(Long orderId, String orderCode, String productName) {
        Long userId = Claims.getUserIdFromJwt();
        String role = Claims.getRoleFromJwt();

        List<OrderItem> orderItems;
        if (orderId == null && orderCode == null && productName == null) {
            orderItems = orderItemRepository.findAllByUserId(userId);
        } else if (orderId != null && orderCode == null && productName == null) {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new DataNotFoundException("Order not found"));

            if ("Customer".equals(role)){
                if (!order.getUser().getId().equals(userId)) {
                    throw new UnauthorizedException("Unauthorized: Order does not belong to the user");
                }
            }

            orderItems = orderItemRepository.findAllByOrderId(orderId);
        } else if (orderId == null && orderCode != null && productName == null) {
            Order order = orderRepository.findByOrderCode(orderCode)
                    .orElseThrow(() -> new DataNotFoundException("Order not found"));

            if ("Customer".equals(role)){
                if (!order.getUser().getId().equals(userId)) {
                    throw new UnauthorizedException("Unauthorized: Order does not belong to the user");
                }
            }

            orderItems = orderItemRepository.findAllByOrderCode(orderCode);
        } else if (orderId == null && orderCode == null) {
            orderItems = orderItemRepository.findAllByUserIdAndProductName(userId, productName);
        } else {
            throw new IllegalArgumentException("Invalid filter combination");
        }

        return orderItems.stream().map(GetOrderItemResponseDTO::new).toList();
    }

    @Override
    @Transactional
    public Optional<GetOrderItemResponseDTO> getByIdAndOrderId(Long id, Long orderId) {
        Long userId = Claims.getUserIdFromJwt();
        String role = Claims.getRoleFromJwt();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new DataNotFoundException("Order not found"));

        if ("Customer".equals(role)){
            if (!order.getUser().getId().equals(userId)) {
                throw new UnauthorizedException("Unauthorized: Order does not belong to the user");
            }
        }

        Optional<OrderItem> orderItemById = orderItemRepository.findByOrderIdAndId(orderId, id);
        if (orderItemById.isEmpty()){
            throw new DataNotFoundException("Order item not found !");
        }

        return orderItemById.map(GetOrderItemResponseDTO::new);
    }
}
