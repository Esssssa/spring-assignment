package com.nti.practice.dao;

import com.nti.practice.model.Employee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EmployeeDao {

    @PersistenceContext
    private EntityManager em;

    public Employee findById(Long id) {
        return em.find(Employee.class, id);
    }

    public List<Employee> findAll() {
        return em.createQuery("SELECT e FROM Employee e ORDER BY e.id", Employee.class).getResultList();
    }

    public void update(Employee employee) {
        em.merge(employee);
    }
}
