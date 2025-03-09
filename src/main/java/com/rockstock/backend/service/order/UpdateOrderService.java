package com.rockstock.backend.service.order;

import com.rockstock.backend.entity.order.Order;
import com.rockstock.backend.entity.order.OrderStatusList;
import com.rockstock.backend.infrastructure.order.dto.UpdateOrderRequestDTO;

public interface UpdateOrderService {
    Order updateOrderStatus(OrderStatusList status, Long orderId, String orderCode, UpdateOrderRequestDTO req);
}
