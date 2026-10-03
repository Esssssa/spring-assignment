package com.library.model;

import jakarta.persistence.Entity;

@Entity
public class Customer extends Person {

    private String membershipNumber;

    public Customer() {
    }

    public Customer(String name, String email, String membershipNumber) {
        super(name, email);
        this.membershipNumber = membershipNumber;
    }

    public String getMembershipNumber() {
        return membershipNumber;
    }
}
