package com.nti.practice;

import com.nti.practice.config.AppConfig;
import com.nti.practice.dao.DepartmentDao;
import com.nti.practice.dao.EmployeeDao;
import com.nti.practice.model.Department;
import com.nti.practice.model.Employee;
import com.nti.practice.repository.EmployeeRepository;
import com.nti.practice.service.PayrollService;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            DepartmentDao departments = context.getBean(DepartmentDao.class);
            EmployeeDao employees = context.getBean(EmployeeDao.class);
            PayrollService payroll = context.getBean(PayrollService.class);
            EmployeeRepository repository = context.getBean(EmployeeRepository.class);
            Statistics statistics = context.getBean(EntityManagerFactory.class).unwrap(SessionFactory.class).getStatistics();

            String[] names = {"Engineering", "HR", "Finance", "Support"};
            for (int d = 0; d < names.length; d++) {
                Department department = new Department(names[d]);
                for (int e = 1; e <= 3; e++) {
                    department.addEmployee(new Employee(names[d] + " employee " + e, 5000 + d * 1000 + e * 100));
                }
                departments.save(department);
            }

            System.out.println("=== N+1 problem versus JOIN FETCH ===");
            statistics.clear();
            int total = departments.countEmployeesWithNPlusOne();
            System.out.println("Employees counted: " + total + ", statements executed: " + statistics.getPrepareStatementCount());

            statistics.clear();
            total = departments.countEmployeesWithJoinFetch();
            System.out.println("Employees counted: " + total + ", statements executed: " + statistics.getPrepareStatementCount());

            System.out.println("\n=== JPA with PersistenceContext ===");
            System.out.println(employees.findById(1L));
            System.out.println("Total employees: " + employees.findAll().size());

            System.out.println("\n=== Rollback ===");
            System.out.println("Before: " + employees.findById(1L).getSalary() + " and " + employees.findById(2L).getSalary());
            try {
                payroll.transferBonus(1L, 2L, 500, true);
            } catch (RuntimeException e) {
                System.out.println("Caught: " + e.getMessage());
            }
            System.out.println("After rollback: " + employees.findById(1L).getSalary() + " and " + employees.findById(2L).getSalary());
            payroll.transferBonus(1L, 2L, 500, false);
            System.out.println("After commit: " + employees.findById(1L).getSalary() + " and " + employees.findById(2L).getSalary());

            System.out.println("\n=== Spring Data JPA ===");
            System.out.println("Salary above 7000: " + repository.findBySalaryGreaterThan(7000).size());
            System.out.println("In HR: " + repository.findByDepartmentName("HR"));
            System.out.println("With department: " + repository.findWithDepartment("Finance employee 1").getDepartment().getName());
            System.out.println("Count: " + repository.count());
        }
    }
}
