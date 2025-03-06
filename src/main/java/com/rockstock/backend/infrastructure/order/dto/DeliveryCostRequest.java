package com.rockstock.backend.infrastructure.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryCostRequest {
    private String origin;
    private String destination;
    private Integer weight;
    private String courier;
}
