package com.rockstock.backend.infrastructure.order.controller;

import com.rockstock.backend.infrastructure.order.dto.DeliveryCostRequest;
import com.rockstock.backend.infrastructure.order.dto.DeliveryCostResponse;
import com.rockstock.backend.service.order.DeliveryCostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/delivery")
@CrossOrigin(origins = "*") // Enable CORS
public class DeliveryCostController {

    private final DeliveryCostService deliveryCostService;

    public DeliveryCostController(DeliveryCostService deliveryCostService) {
        this.deliveryCostService = deliveryCostService;
    }

    @PostMapping("/calculate")
    public ResponseEntity<DeliveryCostResponse> calculateDeliveryCost(@RequestBody DeliveryCostRequest request) {
        DeliveryCostResponse response = deliveryCostService.calculateDeliveryCost(request);
        return ResponseEntity.ok(response);
    }
}