package com.rockstock.backend.service.order;

import com.rockstock.backend.entity.order.OrderStatusList;
import com.rockstock.backend.infrastructure.order.dto.GetOrderResponseDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface GetOrderService {
    Page<GetOrderResponseDTO> getFilteredOrders(
            Long warehouseId,
            OrderStatusList status,
            Integer page,
            Integer size,
            String sortBy,
            String sortDirection,
            LocalDate startDate,
            LocalDate endDate,
            YearMonth monthYear,
            Integer year
    );
    List<GetOrderResponseDTO> getAllByPaymentMethodName(String methodName);
    Optional<GetOrderResponseDTO> getByOrder(Long orderId, String orderCode);
}
