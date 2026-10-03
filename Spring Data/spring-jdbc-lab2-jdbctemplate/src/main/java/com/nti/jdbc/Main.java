package com.nti.jdbc;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.dao.DuplicateKeyException;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            EmployeeDao dao = context.getBean(EmployeeDao.class);
            PayrollService payroll = context.getBean(PayrollService.class);

            System.out.println("=== CRUD ===");
            int id = dao.insert(new Employee(null, "Lina", 9000, 1));
            System.out.println("Inserted with generated id " + id);
            System.out.println("Found: " + dao.findById(id));
            dao.update(new Employee(id, "Lina", 9500, 1));
            System.out.println("Updated: " + dao.findById(id));
            dao.delete(id);
            System.out.println("All after delete: " + dao.findAll());

            System.out.println("\n=== Salary range and group count ===");
            System.out.println(dao.findBySalaryRange(7000, 8500));
            System.out.println(dao.countByDepartment());

            System.out.println("\n=== Batch insert ===");
            List<Employee> batch = new ArrayList<>();
            for (int i = 1; i <= 100; i++) {
                batch.add(new Employee(null, "Employee " + i, 5000 + i, (i % 3) + 1));
            }
            dao.insertBatch(batch);
            System.out.println("Rows after batch: " + dao.count());
            System.out.println(dao.countByDepartment());

            System.out.println("\n=== Transactions ===");
            System.out.println("Before: " + dao.findById(1).salary() + " and " + dao.findById(2).salary());
            payroll.transferBonus(1, 2, 500, false);
            System.out.println("After commit: " + dao.findById(1).salary() + " and " + dao.findById(2).salary());
            try {
                payroll.transferBonus(1, 2, 500, true);
            } catch (RuntimeException e) {
                System.out.println("Caught: " + e.getMessage());
            }
            System.out.println("After rollback: " + dao.findById(1).salary() + " and " + dao.findById(2).salary());

            System.out.println("\n=== Duplicate key ===");
            try {
                dao.insertWithId(new Employee(1, "Duplicate", 1000, 1));
            } catch (DuplicateKeyException e) {
                System.out.println("An employee with id 1 already exists, nothing was inserted");
            }
        }
    }
}
