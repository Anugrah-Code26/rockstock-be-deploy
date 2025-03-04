package com.rockstock.backend.service.order.impl;

import com.rockstock.backend.common.exceptions.DataNotFoundException;
import com.rockstock.backend.entity.order.Order;
import com.rockstock.backend.entity.order.OrderStatusList;
import com.rockstock.backend.infrastructure.order.dto.GetOrderResponseDTO;
import com.rockstock.backend.infrastructure.order.repository.OrderRepository;
import com.rockstock.backend.infrastructure.user.auth.security.Claims;
import com.rockstock.backend.service.order.GetOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetOrderServiceImpl implements GetOrderService {

    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public List<GetOrderResponseDTO> getFilteredOrders(Long warehouseId, OrderStatusList status) {
        Long userId = Claims.getUserIdFromJwt();
        String role = Claims.getRoleFromJwt();
        List<Long> warehouseIds = Claims.getWarehouseIdsFromJwt();

        List<Order> filteredOrders;

        if ("Super Admin".equals(role) && warehouseId == null && status == null) {
            filteredOrders = orderRepository.findAllWithDetails();
        } else if ("Customer".equals(role)) {
            filteredOrders = status != null
                    ? orderRepository.findAllByUserIdAndStatus(userId, status)
                    : orderRepository.findAllByUserId(userId);
        } else if (warehouseId != null) {
            if ("Warehouse Admin".equals(role) && !warehouseIds.contains(warehouseId)) {
                throw new AuthorizationDeniedException("You do not have access to this warehouse!");
            }
            filteredOrders = (status != null)
                    ? orderRepository.findAllByWarehouseIdAndStatus(warehouseId, status)
                    : orderRepository.findAllByWarehouseId(warehouseId);
        } else if (status != null && "Super Admin".equals(role)) {
            filteredOrders = orderRepository.findAllByStatus(status);
        } else {
            throw new IllegalArgumentException("Invalid filter combination");
        }

        return filteredOrders.stream().map(GetOrderResponseDTO::new).toList();
    }

    @Override
    @Transactional
    public List<GetOrderResponseDTO> getAllByPaymentMethodName(String methodName) {
        List<Order> orders = orderRepository.findAllByPaymentMethodName(methodName);
        if (orders.isEmpty()) {
            throw new DataNotFoundException("No orders found for payment method: " + methodName);
        }
        return orders.stream().map(GetOrderResponseDTO::new).toList();
    }

    @Override
    @Transactional
    public Optional<GetOrderResponseDTO> getByOrder(Long orderId, String orderCode) {
        Long userId = Claims.getUserIdFromJwt();
        String role = Claims.getRoleFromJwt();
        List<Long> warehouseIds = Claims.getWarehouseIdsFromJwt();

        Optional<Order> order = (orderId != null)
                ? orderRepository.findById(orderId)
                : orderRepository.findByOrderCode(orderCode);

        if (order.isEmpty()) {
            throw new DataNotFoundException("Order not found!");
        }

        Order foundOrder = order.get();

        // Customer can only access their own orders
        if ("Customer".equals(role)) {
            Optional<Order> userOwnsOrder = (orderId != null)
                    ? orderRepository.findByIdAndUserId(orderId, userId)
                    : orderRepository.findByOrderCodeAndUserId(orderCode, userId);
            if (userOwnsOrder.isEmpty()) {
                throw new AuthorizationDeniedException("You are not authorized to access this order!");
            }
        }

        // Warehouse Admin can only access orders from warehouses they manage
        if ("Warehouse Admin".equals(role) && !warehouseIds.contains(foundOrder.getWarehouse().getId())) {
            throw new AuthorizationDeniedException("You do not have access to this order!");
        }

        return Optional.of(new GetOrderResponseDTO(foundOrder));
    }
}
