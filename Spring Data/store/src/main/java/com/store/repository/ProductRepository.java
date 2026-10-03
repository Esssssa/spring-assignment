package com.store.repository;

import com.store.model.Category;
import com.store.model.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepository {

    @PersistenceContext
    private EntityManager em;

    public Product save(Product product) {
        em.persist(product);
        return product;
    }

    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(em.find(Product.class, id));
    }

    public Optional<Product> findBySku(String sku) {
        return em.createQuery("SELECT p FROM Product p WHERE p.sku = :sku", Product.class)
                .setParameter("sku", sku)
                .getResultStream()
                .findFirst();
    }

    public List<Product> findByCategory(String categoryName) {
        return em.createQuery("SELECT p FROM Product p JOIN p.categories c WHERE c.name = :name ORDER BY p.id", Product.class)
                .setParameter("name", categoryName)
                .getResultList();
    }

    public List<Product> search(String keyword, BigDecimal minPrice, BigDecimal maxPrice, String category) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Product> query = cb.createQuery(Product.class);
        Root<Product> product = query.from(Product.class);
        List<Predicate> predicates = new ArrayList<>();
        if (keyword != null && !keyword.isBlank()) {
            predicates.add(cb.like(cb.lower(product.get("name")), "%" + keyword.toLowerCase() + "%"));
        }
        if (minPrice != null) {
            predicates.add(cb.greaterThanOrEqualTo(product.<BigDecimal>get("price"), minPrice));
        }
        if (maxPrice != null) {
            predicates.add(cb.lessThanOrEqualTo(product.<BigDecimal>get("price"), maxPrice));
        }
        if (category != null && !category.isBlank()) {
            Join<Product, Category> categories = product.join("categories");
            predicates.add(cb.equal(categories.get("name"), category));
        }
        query.select(product).where(predicates.toArray(new Predicate[0])).orderBy(cb.asc(product.get("id")));
        return em.createQuery(query).getResultList();
    }

    public List<Product> findLowStock(int threshold) {
        return em.createQuery("SELECT p FROM Product p WHERE p.stock < :threshold ORDER BY p.stock", Product.class)
                .setParameter("threshold", threshold)
                .getResultList();
    }

    public List<Product> findPage(int page, int size) {
        return em.createQuery("SELECT p FROM Product p ORDER BY p.id", Product.class)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    public long countAll() {
        return em.createQuery("SELECT COUNT(p) FROM Product p", Long.class).getSingleResult();
    }
}
