package com.nti.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class EmployeeDao {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedTemplate;
    private final EmployeeRowMapper rowMapper = new EmployeeRowMapper();

    @Autowired
    public EmployeeDao(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedTemplate = namedTemplate;
    }

    public int insert(Employee employee) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO employee (name, salary, department_id) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, employee.name());
            ps.setDouble(2, employee.salary());
            ps.setInt(3, employee.departmentId());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    public void insertWithId(Employee employee) {
        jdbcTemplate.update("INSERT INTO employee (id, name, salary, department_id) VALUES (?, ?, ?, ?)",
                employee.id(), employee.name(), employee.salary(), employee.departmentId());
    }

    public Employee findById(int id) {
        return jdbcTemplate.queryForObject("SELECT * FROM employee WHERE id = ?", rowMapper, id);
    }

    public List<Employee> findAll() {
        return jdbcTemplate.query("SELECT * FROM employee ORDER BY id", rowMapper);
    }

    public int update(Employee employee) {
        return jdbcTemplate.update("UPDATE employee SET name = ?, salary = ?, department_id = ? WHERE id = ?",
                employee.name(), employee.salary(), employee.departmentId(), employee.id());
    }

    public int delete(int id) {
        return jdbcTemplate.update("DELETE FROM employee WHERE id = ?", id);
    }

    public List<Employee> findBySalaryRange(double min, double max) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("min", min)
                .addValue("max", max);
        return namedTemplate.query("SELECT * FROM employee WHERE salary BETWEEN :min AND :max ORDER BY salary", params, rowMapper);
    }

    public Map<String, Integer> countByDepartment() {
        return jdbcTemplate.query(
                "SELECT d.name, COUNT(e.id) AS total FROM department d LEFT JOIN employee e ON e.department_id = d.id GROUP BY d.name ORDER BY d.name",
                rs -> {
                    Map<String, Integer> counts = new LinkedHashMap<>();
                    while (rs.next()) {
                        counts.put(rs.getString("name"), rs.getInt("total"));
                    }
                    return counts;
                });
    }

    public int[][] insertBatch(List<Employee> employees) {
        return jdbcTemplate.batchUpdate("INSERT INTO employee (name, salary, department_id) VALUES (?, ?, ?)",
                employees, employees.size(), (ps, employee) -> {
                    ps.setString(1, employee.name());
                    ps.setDouble(2, employee.salary());
                    ps.setInt(3, employee.departmentId());
                });
    }

    public int count() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM employee", Integer.class);
    }
}
