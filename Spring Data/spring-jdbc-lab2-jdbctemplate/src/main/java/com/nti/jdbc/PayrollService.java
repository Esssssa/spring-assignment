package com.nti.jdbc;

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
    public void transferBonus(int fromId, int toId, double amount, boolean failMidway) {
        Employee from = dao.findById(fromId);
        Employee to = dao.findById(toId);
        dao.update(new Employee(from.id(), from.name(), from.salary() - amount, from.departmentId()));
        if (failMidway) {
            throw new RuntimeException("Failure after the first update");
        }
        dao.update(new Employee(to.id(), to.name(), to.salary() + amount, to.departmentId()));
    }
}
