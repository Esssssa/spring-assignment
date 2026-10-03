package com.store.dto;

import java.math.BigDecimal;

public record ItemLine(String sku, int quantity, BigDecimal unitPrice) {
}
