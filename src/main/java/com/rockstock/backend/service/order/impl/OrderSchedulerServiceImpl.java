package com.rockstock.backend.service.order.impl;

import com.rockstock.backend.entity.order.Order;
import com.rockstock.backend.entity.order.OrderStatusList;
import com.rockstock.backend.infrastructure.order.repository.OrderRepository;
import com.rockstock.backend.service.order.OrderSchedulerService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderSchedulerServiceImpl implements OrderSchedulerService {

    private final OrderRepository orderRepository;

    @Override
    @Transactional
    @Scheduled(fixedRate = 600000) // Runs every 10 minutes to check for expired payments
    public void cancelExpiredOrders() {
        OffsetDateTime oneHourAgo = OffsetDateTime.now().minusHours(1);

        List<Order> expiredOrders = orderRepository.findByStatusAndCreatedAtBefore(
                OrderStatusList.WAITING_FOR_PAYMENT, oneHourAgo);

        if (!expiredOrders.isEmpty()) {
            expiredOrders.forEach(order -> order.setStatus(OrderStatusList.CANCELED));
            orderRepository.saveAll(expiredOrders);
            System.out.println(expiredOrders.size() + " orders were automatically canceled.");
        }
    }

    @Override
    @Transactional
    @Scheduled(fixedRate = 600000) // Runs every 10 minutes to check for expired payments
    public void autoCompleteOrders() {
        OffsetDateTime twoDaysAgo = OffsetDateTime.now().minusDays(2);

        List<Order> completedOrders = orderRepository.findByStatusAndCreatedAtBefore(
                OrderStatusList.ON_DELIVERY, twoDaysAgo);

        if (!completedOrders.isEmpty()) {
            completedOrders.forEach(order -> order.setStatus(OrderStatusList.COMPLETED));
            orderRepository.saveAll(completedOrders);
            System.out.println(completedOrders.size() + " orders were automatically completed.");
        }
    }
}
