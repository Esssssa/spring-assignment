package com.example.shop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public Product create(String name, double price) {
        log.debug("Creating product {} with price {}", name, price);
        Product product = repository.save(name, price);
        log.info("Product {} created with id {}", product.name(), product.id());
        return product;
    }

    public List<Product> findAll() {
        log.debug("Listing all products");
        return repository.findAll();
    }
}
