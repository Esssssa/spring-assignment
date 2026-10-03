package com.store.service;

import com.store.exception.NotFoundException;
import com.store.model.Category;
import com.store.model.Product;
import com.store.repository.CategoryRepository;
import com.store.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class ProductService {

    private final ProductRepository products;
    private final CategoryRepository categories;

    public ProductService(ProductRepository products, CategoryRepository categories) {
        this.products = products;
        this.categories = categories;
    }

    @Transactional
    public Product addProduct(String sku, String name, BigDecimal price, int stock, String... categoryNames) {
        Product product = new Product(sku, name, price, stock);
        for (String categoryName : categoryNames) {
            Category category = categories.findByName(categoryName).orElseGet(() -> categories.save(new Category(categoryName)));
            product.addCategory(category);
        }
        return products.save(product);
    }

    @Transactional
    public void restock(Long productId, int quantity) {
        Product product = products.findById(productId).orElseThrow(() -> new NotFoundException("Product", productId));
        product.setStock(product.getStock() + quantity);
    }

    @Transactional
    public void changePrice(Long productId, BigDecimal newPrice) {
        Product product = products.findById(productId).orElseThrow(() -> new NotFoundException("Product", productId));
        product.setPrice(newPrice);
    }
}
