package com.rockstock.backend.service.payment.impl;

import com.midtrans.Config;
import com.midtrans.httpclient.SnapApi;
import com.midtrans.httpclient.error.MidtransError;
import com.rockstock.backend.entity.order.Order;
import com.rockstock.backend.entity.order.OrderStatusList;
import com.rockstock.backend.infrastructure.order.repository.OrderRepository;
import com.rockstock.backend.service.order.UpdateOrderService;
import com.rockstock.backend.service.payment.MidtransPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MidtransPaymentServiceImpl implements MidtransPaymentService {

    private final Config midtransConfig;
    private final OrderRepository orderRepository;
    private final UpdateOrderService updateOrderService;

    @Override
    @Transactional
    public Map<String, String> createTransactionToken(String orderCode, Double amount) throws MidtransError {
        // Transaction details
        Map<String, Object> transactionDetails = new HashMap<>();
        transactionDetails.put("order_id", orderCode);
        transactionDetails.put("gross_amount", amount);

        // Credit card settings
        Map<String, Object> creditCard = new HashMap<>();
        creditCard.put("secure", true);

        // Main request body
        Map<String, Object> params = new HashMap<>();
        params.put("transaction_details", transactionDetails);
        params.put("credit_card", creditCard);

        String token = SnapApi.createTransactionToken(params, midtransConfig);
        String redirectUrl = "https://app.sandbox.midtrans.com/snap/v2/vtweb/" + token;

        Map<String, String> result = new HashMap<>();
        result.put("token", token);
        result.put("redirect_url", redirectUrl);
        return result;
    }

    @Override
    @Transactional
    public void processPaymentNotification(Map<String, Object> payload) {
        System.out.println("🔍 Received payment notification: " + payload);

        String orderCode = (String) payload.get("order_id");
        String transactionStatus = (String) payload.get("transaction_status");

        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new RuntimeException("❌ Order not found with Order Code: " + orderCode));

        System.out.println("🔍 Found order: " + orderCode + " with current status: " + order.getStatus());

        switch (transactionStatus) {
            case "settlement", "capture", "success" -> {
                if (order.getStatus() == OrderStatusList.WAITING_FOR_PAYMENT) {
                    System.out.println("✅ Payment successful. Updating order status to PROCESSING...");
                    order.setStatus(OrderStatusList.PROCESSING);
                    orderRepository.save(order); // Ensure the update is persisted
                    System.out.println("✅ Order " + orderCode + " updated to PROCESSING in the database.");
                } else {
                    System.out.println("⚠️ Order " + orderCode + " is already processed. No update needed.");
                }
            }
            case "pending" -> {
                System.out.println("ℹ️ Order " + orderCode + " is still pending payment. No status change.");
            }
            case "deny", "cancel", "expire", "failure" -> {
                System.out.println("❌ Payment failed. Updating order status to CANCELED...");
                order.setStatus(OrderStatusList.CANCELED);
                orderRepository.save(order);
                System.out.println("✅ Order " + orderCode + " updated to CANCELED in the database.");
            }
            case "refund", "chargeback" -> {
                System.out.println("⚠️ Order " + orderCode + " has been refunded or chargeback initiated.");
            }
            default -> {
                System.out.println("⚠️ Unhandled transaction status: " + transactionStatus);
            }
        }
    }

}
