package com.store.repository;

import com.store.model.Customer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CustomerRepository {

    @PersistenceContext
    private EntityManager em;

    public Customer save(Customer customer) {
        em.persist(customer);
        return customer;
    }

    public Optional<Customer> findById(Long id) {
        return Optional.ofNullable(em.find(Customer.class, id));
    }

    public Optional<Customer> findByEmail(String email) {
        return em.createQuery("SELECT c FROM Customer c WHERE c.email = :email", Customer.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst();
    }

    public List<Customer> findAll() {
        return em.createQuery("SELECT c FROM Customer c ORDER BY c.id", Customer.class).getResultList();
    }
}
