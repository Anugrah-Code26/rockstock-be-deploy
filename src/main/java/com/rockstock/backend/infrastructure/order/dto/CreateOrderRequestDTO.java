package com.rockstock.backend.infrastructure.order.dto;

import com.rockstock.backend.entity.geolocation.Address;
import com.rockstock.backend.entity.order.Order;
import com.rockstock.backend.entity.payment.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequestDTO {

    @NotNull(message = "Delivery cost is required")
    private BigDecimal deliveryCost;

    @NotNull
    private Long addressId;

    private Long paymentMethodId;

    public Order toEntity(
            Address address
    ) {
        Order order = new Order();

        order.setDeliveryCost(deliveryCost);
        order.setAddress(address);

        return order;
    }
}
