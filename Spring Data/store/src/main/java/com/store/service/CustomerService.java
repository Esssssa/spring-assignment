package com.store.service;

import com.store.exception.DuplicateCustomerException;
import com.store.model.Address;
import com.store.model.Customer;
import com.store.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

    private final CustomerRepository customers;

    public CustomerService(CustomerRepository customers) {
        this.customers = customers;
    }

    @Transactional
    public Customer register(String name, String email, Address address) {
        if (customers.findByEmail(email).isPresent()) {
            throw new DuplicateCustomerException(email);
        }
        return customers.save(new Customer(name, email, address));
    }
}
