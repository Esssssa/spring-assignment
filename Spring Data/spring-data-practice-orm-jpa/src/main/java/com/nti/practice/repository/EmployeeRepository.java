package com.nti.practice.repository;

import com.nti.practice.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findBySalaryGreaterThan(double salary);

    List<Employee> findByDepartmentName(String name);

    @Query("SELECT e FROM Employee e JOIN FETCH e.department WHERE e.name = :name")
    Employee findWithDepartment(@Param("name") String name);
}
