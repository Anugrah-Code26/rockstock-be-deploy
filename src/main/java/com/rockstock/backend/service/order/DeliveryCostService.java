package com.rockstock.backend.service.order;

import com.rockstock.backend.infrastructure.order.dto.DeliveryCostRequest;
import com.rockstock.backend.infrastructure.order.dto.DeliveryCostResponse;

public interface DeliveryCostService {
    DeliveryCostResponse calculateDeliveryCost(DeliveryCostRequest request);
}
