package com.nti.practice.dao;

import com.nti.practice.model.Department;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class DepartmentDao {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void save(Department department) {
        em.persist(department);
    }

    @Transactional(readOnly = true)
    public int countEmployeesWithNPlusOne() {
        List<Department> departments = em.createQuery("SELECT d FROM Department d", Department.class).getResultList();
        int total = 0;
        for (Department department : departments) {
            total += department.getEmployees().size();
        }
        return total;
    }

    @Transactional(readOnly = true)
    public int countEmployeesWithJoinFetch() {
        List<Department> departments = em.createQuery("SELECT DISTINCT d FROM Department d JOIN FETCH d.employees", Department.class).getResultList();
        int total = 0;
        for (Department department : departments) {
            total += department.getEmployees().size();
        }
        return total;
    }
}
