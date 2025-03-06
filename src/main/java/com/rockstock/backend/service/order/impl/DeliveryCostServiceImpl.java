package com.rockstock.backend.service.order.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rockstock.backend.infrastructure.order.dto.DeliveryCostRequest;
import com.rockstock.backend.infrastructure.order.dto.DeliveryCostResponse;
import com.rockstock.backend.service.order.DeliveryCostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryCostServiceImpl implements DeliveryCostService {

    @Value("${rajaongkir.api.url}")
    private String apiUrl;

    @Value("${rajaongkir.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    @Override
    public DeliveryCostResponse calculateDeliveryCost(DeliveryCostRequest request) {
        // Construct URL with query parameters instead of sending JSON body
        String url = String.format(
                "%s/calculate/domestic-cost?origin=%s&destination=%s&weight=%d&courier=%s",
                apiUrl, request.getOrigin(), request.getDestination(), request.getWeight(), request.getCourier()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("key", apiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers); // No body, just headers

        try {
            log.info("🚀 Sending request to RajaOngkir: {}", url);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            log.info("✅ Response from RajaOngkir: {}", response.getBody());

            return new ObjectMapper().readValue(response.getBody(), DeliveryCostResponse.class);
        } catch (HttpClientErrorException e) {
            log.error("❌ Error from RajaOngkir: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Error fetching delivery cost: " + e.getResponseBodyAsString());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
