package com.rockstock.backend.service.payment;

import com.midtrans.httpclient.error.MidtransError;

import java.util.Map;

public interface MidtransPaymentService {
    Map<String, String> createTransactionToken(String orderCode, Double amount) throws MidtransError;
    void processPaymentNotification(Map<String, Object> payload);
}
