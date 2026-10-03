package com.example.productapi;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ProductRepository {

    private final Map<Long, Product> products = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public ProductRepository() {
        save(new Product(null, "Keyboard", 49.99));
        save(new Product(null, "Mouse", 19.99));
    }

    public Product save(Product product) {
        product.setId(nextId.getAndIncrement());
        products.put(product.getId(), product);
        return product;
    }

    public List<Product> findAll() {
        List<Product> all = new ArrayList<>(products.values());
        all.sort((a, b) -> Long.compare(a.getId(), b.getId()));
        return all;
    }

    public Product findById(Long id) {
        return products.get(id);
    }

    public Product update(Long id, Product product) {
        product.setId(id);
        return products.replace(id, product) == null ? null : product;
    }

    public boolean delete(Long id) {
        return products.remove(id) != null;
    }
}
