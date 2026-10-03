# Day 3, Practice Exercises

One project covering the exercises, using `LocalContainerEntityManagerFactoryBean` and `JpaTransactionManager`.

- Department 1 to N Employee mapped with Hibernate through JPA.
- N+1: `countEmployeesWithNPlusOne` runs 5 statements for 4 departments, and `countEmployeesWithJoinFetch` runs 1.
- `EmployeeDao` uses `@PersistenceContext`.
- `PayrollService` throws midway and `Main` shows that the first update is rolled back.
- Bonus: `EmployeeRepository` is a Spring Data JPA interface with derived queries and one `@Query`, replacing the DAO code with a few lines.

The JdbcTemplate version of the Employee DAO with batch insert is in `spring-jdbc-lab2-jdbctemplate`.
