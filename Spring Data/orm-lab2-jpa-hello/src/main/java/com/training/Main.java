package com.training;

import com.training.dao.EmployeeJpaDao;
import com.training.model.Employee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class Main {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("empPU");
        System.out.println("EntityManagerFactory built");

        System.out.println("\n=== Hello JPA ===");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Employee hello = new Employee("Hello", "Training", 1000);
        em.persist(hello);
        em.getTransaction().commit();
        Employee found = em.find(Employee.class, hello.getId());
        System.out.println("Found by id: " + found.getName());
        em.close();

        EmployeeJpaDao dao = new EmployeeJpaDao(emf);

        System.out.println("\n=== Create ===");
        dao.save(new Employee("Ahmed", "Engineering", 8000));
        dao.save(new Employee("Sara", "HR", 6500));
        dao.save(new Employee("Omar", "Finance", 7200));

        System.out.println("\n=== Read ===");
        System.out.println(dao.findById(2));
        dao.findAll().forEach(System.out::println);

        System.out.println("\n=== Update ===");
        dao.giveRaise(2, 1500);
        System.out.println(dao.findById(2));

        System.out.println("\n=== Delete ===");
        dao.delete(4);
        dao.findAll().forEach(System.out::println);
        System.out.println("Employee 4 after delete: " + dao.findById(4));

        System.out.println("\n=== Detached stretch ===");
        EntityManager closed = emf.createEntityManager();
        Employee detached = closed.find(Employee.class, 2);
        closed.close();
        detached.setSalary(1);
        EntityManager fresh = emf.createEntityManager();
        System.out.println("Detached change not saved: " + fresh.find(Employee.class, 2).getSalary());
        fresh.close();

        emf.close();
    }
}
