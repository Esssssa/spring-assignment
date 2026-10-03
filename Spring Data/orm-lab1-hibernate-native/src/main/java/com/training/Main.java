package com.training;

import com.training.dao.EmployeeDao;
import com.training.model.Department;
import com.training.model.Employee;
import com.training.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class Main {

    public static void main(String[] args) {
        SessionFactory factory = HibernateUtil.getSessionFactory();
        factory.openSession().close();
        System.out.println("SessionFactory built, smoke test passed");

        EmployeeDao dao = new EmployeeDao();

        System.out.println("\n=== Create ===");
        dao.save(new Employee("Ahmed", "Engineering", 8000));
        dao.save(new Employee("Sara", "HR", 6500));
        dao.save(new Employee("Omar", "Finance", 7200));

        System.out.println("\n=== Read ===");
        System.out.println(dao.findById(1));
        dao.findAll().forEach(System.out::println);

        System.out.println("\n=== Update ===");
        dao.giveRaise(1, 1500);
        System.out.println(dao.findById(1));

        System.out.println("\n=== Delete ===");
        dao.delete(3);
        dao.findAll().forEach(System.out::println);
        System.out.println("Employee 3 after delete: " + dao.findById(3));

        lifecycle(factory);
        firstLevelCache(factory);
        department(factory);
    }

    private static void lifecycle(SessionFactory factory) {
        System.out.println("\n=== Lifecycle ===");
        Employee employee = new Employee("Lina", "Support", 5000);
        System.out.println("Transient: " + employee);

        Employee detached;
        try (Session session = factory.openSession()) {
            session.beginTransaction();
            session.persist(employee);
            System.out.println("Persistent: " + employee);
            Employee again = session.get(Employee.class, employee.getId());
            System.out.println("Same instance in the session: " + (again == employee));
            session.getTransaction().commit();
            detached = employee;
        }

        detached.setSalary(99999);
        System.out.println("Detached object mutated to " + detached.getSalary());
        try (Session session = factory.openSession()) {
            System.out.println("Reloaded in a new session: " + session.get(Employee.class, detached.getId()));
        }

        try (Session session = factory.openSession()) {
            session.beginTransaction();
            Employee managed = session.get(Employee.class, detached.getId());
            session.remove(managed);
            session.getTransaction().commit();
        }
        try (Session session = factory.openSession()) {
            System.out.println("After remove: " + session.get(Employee.class, detached.getId()));
        }
    }

    private static void firstLevelCache(SessionFactory factory) {
        System.out.println("\n=== First level cache ===");
        try (Session session = factory.openSession()) {
            Employee first = session.get(Employee.class, 1);
            Employee second = session.get(Employee.class, 1);
            System.out.println("Same reference: " + (first == second));
        }
        try (Session session = factory.openSession()) {
            System.out.println("New session loads again: " + session.get(Employee.class, 1));
        }
    }

    private static void department(SessionFactory factory) {
        System.out.println("\n=== Department stretch ===");
        try (Session session = factory.openSession()) {
            session.beginTransaction();
            session.persist(new Department("Engineering"));
            session.persist(new Department("HR"));
            session.getTransaction().commit();
            session.createQuery("from Department", Department.class).list().forEach(System.out::println);
        }
    }
}
