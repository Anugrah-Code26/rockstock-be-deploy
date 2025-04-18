package com.rockstock.backend.service.order.impl;

import com.rockstock.backend.common.exceptions.DataNotFoundException;
import com.rockstock.backend.entity.order.Order;
import com.rockstock.backend.entity.order.OrderStatusList;
import com.rockstock.backend.infrastructure.order.dto.GetOrderResponseDTO;
import com.rockstock.backend.infrastructure.order.repository.OrderRepository;
import com.rockstock.backend.infrastructure.user.auth.security.Claims;
import com.rockstock.backend.service.order.GetOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetOrderServiceImpl implements GetOrderService {

    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public Page<GetOrderResponseDTO> getFilteredOrders(
            Long warehouseId,
            OrderStatusList status,
            Integer page,
            Integer size,
            String sortBy,
            String sortDirection,
            LocalDate startDate,
            LocalDate endDate,
            YearMonth monthYear,
            Integer year) {

        Long userId = Claims.getUserIdFromJwt();
        String role = Claims.getRoleFromJwt();
        List<Long> warehouseIds = Claims.getWarehouseIdsFromJwt();

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("DESC".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC,
                        sortBy != null ? sortBy : "createdAt"));

        Specification<Order> spec = Specification.where(null);

        // Role-based access control
        if ("Customer".equals(role)) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("user").get("id"), userId));
        } else if (warehouseId != null) {
            if ("Warehouse Admin".equals(role) && !warehouseIds.contains(warehouseId)) {
                throw new AuthorizationDeniedException("You do not have access to this warehouse!");
            }
            spec = spec.and((root, query, cb) -> cb.equal(root.get("warehouse").get("id"), warehouseId));
        }

        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }

        // Date range filter (convert LocalDate to OffsetDateTime)
        if (startDate != null && endDate != null) {
            OffsetDateTime startDateTime = startDate.atStartOfDay(ZoneOffset.UTC).toOffsetDateTime();
            OffsetDateTime endDateTime = endDate.atTime(23, 59, 59).atOffset(ZoneOffset.UTC);
            spec = spec.and((root, query, cb) -> cb.between(root.get("createdAt"), startDateTime, endDateTime));
        }

        // Filter by month and year
        if (monthYear != null) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.function("MONTH", Integer.class, root.get("createdAt")), monthYear.getMonthValue()))
                    .and((root, query, cb) -> cb.equal(cb.function("YEAR", Integer.class, root.get("createdAt")), monthYear.getYear()));
        }

        // Filter by year
        if (year != null) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.function("YEAR", Integer.class, root.get("createdAt")), year));
        }

        Page<Order> filteredOrders = orderRepository.findAll(spec, pageable);
        return filteredOrders.map(GetOrderResponseDTO::new);
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
