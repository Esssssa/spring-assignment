package com.library.model;

import jakarta.persistence.Entity;

@Entity
public class Employee extends Person {

    private String position;

    private double salary;

    public Employee() {
    }

    public Employee(String name, String email, String position, double salary) {
        super(name, email);
        this.position = position;
        this.salary = salary;
    }

    public String getPosition() {
        return position;
    }

    public double getSalary() {
        return salary;
    }
}
