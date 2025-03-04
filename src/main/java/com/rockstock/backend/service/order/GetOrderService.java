package com.rockstock.backend.service.order;

import com.rockstock.backend.entity.order.OrderStatusList;
import com.rockstock.backend.infrastructure.order.dto.GetOrderResponseDTO;

import java.util.List;
import java.util.Optional;

public interface GetOrderService {
    List<GetOrderResponseDTO> getFilteredOrders(Long warehouseId, OrderStatusList status);
    List<GetOrderResponseDTO> getAllByPaymentMethodName(String methodName);
    Optional<GetOrderResponseDTO> getByOrder(Long orderId, String orderCode);
}
