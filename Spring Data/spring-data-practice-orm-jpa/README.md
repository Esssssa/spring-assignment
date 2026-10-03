# day 3 practice exercises

all in one project, spring + LocalContainerEntityManagerFactoryBean + JpaTransactionManager.

- Department 1-N Employee
- n+1: countEmployeesWithNPlusOne runs 5 statements for 4 departments, the join fetch version runs 1
- EmployeeDao uses @PersistenceContext
- PayrollService throws in the middle and Main shows the first update got rolled back
- bonus: EmployeeRepository is spring data jpa (derived queries + one @Query), way less code than the dao

the jdbctemplate version with the batch insert is in spring-jdbc-lab2-jdbctemplate.
