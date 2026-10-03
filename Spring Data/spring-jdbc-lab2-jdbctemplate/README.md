# day 3 lab 2 - spring jdbc

- EmployeeRowMapper + EmployeeDao with insert (gets the id back with KeyHolder), findById, findAll, update, delete
- findBySalaryRange uses NamedParameterJdbcTemplate, countByDepartment uses a ResultSetExtractor
- insertBatch with batchUpdate, Main inserts 100 employees
- PayrollService.transferBonus is @Transactional. Main calls it once normally and once with a forced RuntimeException after the first update, then prints both salaries so you can see the rollback worked
- Main also catches DuplicateKeyException when inserting an id that already exists
