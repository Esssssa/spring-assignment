package com.store.repository;

import com.store.model.Order;
import com.store.model.OrderStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class OrderRepository {

    @PersistenceContext
    private EntityManager em;

    public Order save(Order order) {
        em.persist(order);
        return order;
    }

    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(em.find(Order.class, id));
    }

    public Optional<Order> findByIdWithItems(Long id) {
        return em.createQuery("SELECT DISTINCT o FROM Order o JOIN FETCH o.customer LEFT JOIN FETCH o.payment LEFT JOIN FETCH o.items i LEFT JOIN FETCH i.product WHERE o.id = :id", Order.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst();
    }

    public List<Order> findByCustomer(Long customerId) {
        return em.createQuery("SELECT o FROM Order o WHERE o.customer.id = :id ORDER BY o.id", Order.class)
                .setParameter("id", customerId)
                .getResultList();
    }

    public List<Order> findByStatus(OrderStatus status) {
        return em.createQuery("SELECT o FROM Order o WHERE o.status = :status ORDER BY o.id", Order.class)
                .setParameter("status", status)
                .getResultList();
    }
}
