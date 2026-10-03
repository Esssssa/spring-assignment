package com.nti.aop;

import org.springframework.stereotype.Service;

@Service
public class AccountService {

    public double getBalance(String id) {
        return 1500.0;
    }

    public void withdraw(String id, double amount) {
        if (amount > 1500) {
            throw new IllegalArgumentException("Insufficient funds for " + id);
        }
        System.out.println("Withdrew " + amount + " from " + id);
    }
}
