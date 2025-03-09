package com.rockstock.backend.infrastructure.order.controller;

import com.midtrans.httpclient.error.MidtransError;
import com.rockstock.backend.common.response.ApiResponse;
import com.rockstock.backend.entity.order.OrderStatusList;
import com.rockstock.backend.infrastructure.order.dto.CreateOrderRequestDTO;
import com.rockstock.backend.infrastructure.order.dto.GetOrderItemResponseDTO;
import com.rockstock.backend.infrastructure.order.dto.GetOrderResponseDTO;
import com.rockstock.backend.infrastructure.order.dto.UpdateOrderRequestDTO;
import com.rockstock.backend.service.order.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final CreateOrderService createOrderService;
    private final GetOrderService getOrderService;
    private final GetOrderItemService getOrderItemService;
    private final UpdateOrderService updateOrderService;

    // Create
    @PostMapping
    public ResponseEntity<?> createOrder(@Valid @RequestBody CreateOrderRequestDTO req) throws MidtransError {
        return ApiResponse.success(HttpStatus.OK.value(), "Create order success!", createOrderService.createOrder(req));
    }

    // Read / Get
    // Order
    @GetMapping
    public ResponseEntity<?> getFilteredOrders(
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) OrderStatusList status,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) YearMonth monthYear,
            @RequestParam(required = false) Integer year
    ) {
        Page<GetOrderResponseDTO> filteredOrders = getOrderService.getFilteredOrders(
                warehouseId, status, page, size, sortBy, sortDirection, startDate, endDate, monthYear, year
        );
        return ApiResponse.success(HttpStatus.OK.value(), "Orders retrieved successfully!", filteredOrders);
    }

    @GetMapping("/order")
    public ResponseEntity<?> getByOrder(
            @RequestParam(required = false) Long orderId,
            @RequestParam(required = false) String orderCode
    ) {
        Optional<GetOrderResponseDTO> filteredOrder = getOrderService.getByOrder(orderId, orderCode);
        return ApiResponse.success(HttpStatus.OK.value(), "Order items retrieved successfully!", filteredOrder);
    }

    // Order Item
    @GetMapping("/items")
    public ResponseEntity<?> getFilteredOrderItems(
            @RequestParam(required = false) Long orderId,
            @RequestParam(required = false) String orderCode,
            @RequestParam(required = false) String productName
    ) {
        List<GetOrderItemResponseDTO> orderItems = getOrderItemService.getFilteredOrderItems(orderId, orderCode, productName);
        return ApiResponse.success(HttpStatus.OK.value(), "Order items retrieved successfully!", orderItems);
    }

    @GetMapping("/items/order-item")
    public ResponseEntity<?> getByIdAndOrderId(@RequestParam Long id, @RequestParam Long orderId) {
        return ApiResponse.success(HttpStatus.OK.value(), "Order items retrieved successfully!", getOrderItemService.getByIdAndOrderId(id, orderId));
    }

    // Update
    @PatchMapping("/statuses/status")
    public ResponseEntity<?> updateOrderStatus(
            @RequestParam(required = false) Long orderId,
            @RequestParam(required = false) String orderCode,
            @RequestParam("newStatus") OrderStatusList newStatus,
            @RequestPart(value = "paymentProof", required = false) MultipartFile paymentProof) {

        UpdateOrderRequestDTO req = new UpdateOrderRequestDTO();
        req.setPaymentProof(paymentProof);

        return ApiResponse.success(HttpStatus.OK.value(), "Order status updated successfully!", updateOrderService.updateOrderStatus(newStatus, orderId, orderCode, req));
    }
}
