package com.rockstock.backend.infrastructure.order.dto;

import com.rockstock.backend.entity.order.Order;
import com.rockstock.backend.entity.order.OrderStatusList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetOrderResponseDTO {

    private Long orderId;
    private String orderCode;
    private String paymentProof;
    private BigDecimal deliveryCost;
    private BigDecimal totalPrice;
    private BigDecimal totalPayment;
    private String formattedCreatedAt;
    private String formattedUpdatedAt;
    private Long userId;
    private String addressDetail;
    private String addressSubDistrict;
    private String warehouseName;
    private OrderStatusList status;
    private String paymentMethod;
    private GetOrderItemResponseDTO firstOrderItem;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd MMMM yyyy");

    public GetOrderResponseDTO(Order order) {
        this.orderId = order.getId();
        this.orderCode = order.getOrderCode();
        this.paymentProof = order.getPaymentProof();
        this.deliveryCost = order.getDeliveryCost();
        this.totalPrice = order.getTotalPrice();
        this.totalPayment = order.getTotalPayment();
        this.formattedCreatedAt = formatDate(order.getCreatedAt());
        this.formattedUpdatedAt = formatDate(order.getUpdatedAt());
        this.userId = order.getUser().getId();
        this.addressDetail = order.getAddress().getAddressDetail();
        this.addressSubDistrict = order.getAddress().getSubDistrict().getName();
        this.warehouseName = order.getWarehouse().getName();
        this.status = order.getStatus();
        this.paymentMethod = order.getPaymentMethod().getName();
        this.firstOrderItem = order.getOrderItems().stream().findFirst().map(GetOrderItemResponseDTO::new).orElse(null);
    }

    private String formatDate(OffsetDateTime dateTime) {
        return dateTime != null ? dateTime.format(FORMATTER) : null;
    }
}
