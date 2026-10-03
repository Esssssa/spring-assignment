# Day 3, Lab 1: JDBC in Spring

- `schema.sql` and `data.sql` create `department` and `employee` with 3 departments and 6 employees.
- `DataSourceConfig` has a Hikari pool of 5 under `@Profile("prod")` and an `EmbeddedDatabaseBuilder` under `@Profile("dev")`.
- `PlainEmployeeDao` uses `DataSourceUtils`, `PreparedStatement` and `ResultSet` for `findById`, `findAll` and `count`.
- `Main` prints all employees and the `DataSource` class. The `dev` profile prints `EmbeddedDatabaseFactory$EmbeddedDataSourceProxy` and `prod` prints `com.zaxxer.hikari.HikariDataSource`.

```bash
mvn compile exec:java -Dexec.mainClass=com.nti.jdbc.Main -Dexec.args=prod
```
