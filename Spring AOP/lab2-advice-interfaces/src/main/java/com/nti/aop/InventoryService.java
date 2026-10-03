package com.nti.aop;

public interface InventoryService {
    int checkStock(String sku);

    void reserveStock(String sku, int qty);
}
