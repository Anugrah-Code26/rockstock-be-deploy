package com.rockstock.backend.infrastructure.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true) // Ignore unexpected fields like "meta"
public class DeliveryCostResponse {
    private int code;
    private String message;

    @JsonProperty("data") // Map "data" field from the API response to results
    private List<DeliveryService> results;

    @Setter
    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true) // Prevents errors if unexpected fields exist
    public static class DeliveryService {
        private String service;
        private String description;
        private int cost;
        private String etd;
    }
}
