# Spring Assignment

One directory per task found in the course PDFs. Every project is a standalone Maven project.

| Topic | Directory | Source | What it covers |
|---|---|---|---|
| Spring AOP | `Spring AOP/lab1-jdk-dynamic-proxy` | Session 1, Lab 1 | `InvocationHandler`, `Proxy.newProxyInstance`, logging and timing |
| Spring AOP | `Spring AOP/lab2-advice-interfaces` | Session 1, Lab 2 | Before, AfterReturning, Throws, Around advice, `ProxyFactory`, `ProxyFactoryBean`, `NameMatchMethodPointcut` |
| Spring AOP | `Spring AOP/lab2-aspectj-annotations` | Session 2, Lab 2 | The five advice annotations, single `@Around`, custom `@Cacheable` and `@Timed` |
| Spring Boot | `Spring Boot/session1-task-tracker-api` | Session 1, Lab | In-memory REST API, `@Value`, 409 limit, `?limit=` |
| Spring Boot | `Spring Boot/session2-config-logging-actuator` | Session 2 | Profiles, `@ConfigurationProperties`, logging levels, DevTools, Actuator |
| Spring MVC | `Spring MVC/task-mvc-jsp` | `Task.txt` | JSP list, details, search, create form, validation, exception handling |
| Spring MVC | `Spring MVC/product-rest-api` | Session 3, Lab 3 | REST CRUD, validation errors, `@RestControllerAdvice`, `consumes`, API key interceptor, response wrapper |
| Spring Data | `Spring Data/orm-lab1-hibernate-native` | ORM Day 1, Lab 1 | Hibernate native CRUD, lifecycle, first level cache |
| Spring Data | `Spring Data/orm-lab2-jpa-hello` | ORM Day 1, Lab 2 | JPA `persistence.xml` and `EntityManager` CRUD |
| Spring Data | `Spring Data/jpa-library-management` | JPA Day 2, Tasks 1 and 2 (and Hands-On Lab 2) | Relationships, cascade, orphan removal, inheritance, JPQL, Criteria |
| Spring Data | `Spring Data/spring-jdbc-lab1-datasource` | Day 3, Lab 1 | `DataSource` beans, Hikari versus embedded, plain JDBC DAO |
| Spring Data | `Spring Data/spring-jdbc-lab2-jdbctemplate` | Day 3, Lab 2 | `JdbcTemplate`, named parameters, batch, `@Transactional`, `DuplicateKeyException` |
| Spring Data | `Spring Data/spring-data-practice-orm-jpa` | Day 3, Practice Exercises | JPA with `@PersistenceContext`, N+1 fix, rollback, Spring Data JPA |

The Spring MVC Session 1 and Four Components PDFs only contain a demo project and recaps, and the Spring MVC Session 2 PDF is covered by `Task.txt`, so they have no separate task directories.

## Running

Plain Java projects:

```bash
mvn compile exec:java -Dexec.mainClass=<main class>
```

Spring Boot projects:

```bash
mvn spring-boot:run
```

Projects with tests:

```bash
mvn test
```

Main classes: `com.nti.aop.Main`, `com.training.Main`, `com.library.Main`, `com.nti.jdbc.Main`, `com.nti.practice.Main`.

`spring-jdbc-lab1-datasource` takes the profile as an argument: `-Dexec.args=dev` or `-Dexec.args=prod`.

Requirements: JDK 21 and Maven 3.9.
