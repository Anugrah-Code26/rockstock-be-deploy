package com.rockstock.backend.entity.stock;

public enum StockChangeType {
    PURCHASE_RECEIVED,
    CUSTOMER_RETURN,
    SALES_DISPATCHED,
    RETURN_TO_SUPPLIER,
    STOCK_ADJUSTMENT,
    DAMAGED_OR_EXPIRED,
    TRANSFER
}