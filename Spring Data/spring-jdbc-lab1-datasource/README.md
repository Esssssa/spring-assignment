# day 3 lab 1 - jdbc in spring

- schema.sql + data.sql: department and employee tables, 3 departments, 6 employees
- DataSourceConfig: hikari pool (size 5) under @Profile("prod"), EmbeddedDatabaseBuilder under @Profile("dev")
- PlainEmployeeDao: plain jdbc with DataSourceUtils, PreparedStatement, ResultSet. findById, findAll, count
- Main prints the employees and the datasource class. dev prints the EmbeddedDataSourceProxy, prod prints com.zaxxer.hikari.HikariDataSource

```bash
mvn compile exec:java -Dexec.mainClass=com.nti.jdbc.Main -Dexec.args=prod
```
