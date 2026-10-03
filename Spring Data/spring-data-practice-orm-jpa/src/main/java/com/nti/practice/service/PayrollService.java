package com.nti.practice.service;

import com.nti.practice.dao.EmployeeDao;
import com.nti.practice.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PayrollService {

    private final EmployeeDao dao;

    @Autowired
    public PayrollService(EmployeeDao dao) {
        this.dao = dao;
    }

    @Transactional
    public void transferBonus(Long fromId, Long toId, double amount, boolean failMidway) {
        Employee from = dao.findById(fromId);
        Employee to = dao.findById(toId);
        from.setSalary(from.getSalary() - amount);
        dao.update(from);
        if (failMidway) {
            throw new RuntimeException("Failure after the first update");
        }
        to.setSalary(to.getSalary() + amount);
        dao.update(to);
    }
}
