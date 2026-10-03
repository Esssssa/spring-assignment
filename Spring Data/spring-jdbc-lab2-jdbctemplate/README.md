# Day 3, Lab 2: Spring JDBC

- `EmployeeRowMapper` and `EmployeeDao` with `insert` (returns the id through `KeyHolder`), `findById`, `findAll`, `update`, `delete`.
- `findBySalaryRange` uses `NamedParameterJdbcTemplate`, `countByDepartment` uses a `ResultSetExtractor`.
- `insertBatch` uses `batchUpdate`; `Main` inserts 100 employees.
- `PayrollService.transferBonus` is `@Transactional`. `Main` calls it with a forced `RuntimeException` after the first update and prints both salaries to show the rollback.
- `Main` catches `DuplicateKeyException` when inserting an id that exists.
