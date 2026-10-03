package com.nti.aop;

public class InventoryServiceImpl implements InventoryService {

    public int checkStock(String sku) {
        return 42;
    }

    public void reserveStock(String sku, int qty) {
        if (qty > 100) {
            throw new IllegalStateException("Cannot reserve more than 100 units of " + sku);
        }
        System.out.println("Reserved " + qty + " of " + sku);
    }
}
