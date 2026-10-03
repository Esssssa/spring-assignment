package com.example.shop;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ProductRepository {

    private final List<Product> products = new ArrayList<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public synchronized Product save(String name, double price) {
        Product product = new Product(nextId.getAndIncrement(), name, price);
        products.add(product);
        return product;
    }

    public synchronized List<Product> findAll() {
        return List.copyOf(products);
    }
}
