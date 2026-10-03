package com.store.dto;

import com.store.model.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

public record OrderSummary(Long orderId, String customer, OrderStatus status, BigDecimal total, List<ItemLine> items) {
}
