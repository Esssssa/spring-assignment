# ORM Day 1, Lab 2: JPA

Same `Employee` class as Lab 1, configured through `META-INF/persistence.xml`. Run `com.training.Main`.

| Operation | Hibernate Native (Lab 1) | JPA (Lab 2) |
|---|---|---|
| Bootstrap class | `SessionFactory` | `EntityManagerFactory` |
| Unit-of-work class | `Session` | `EntityManager` |
| Config file | `hibernate.cfg.xml` | `persistence.xml` |
| Create | `session.persist(e)` | `em.persist(e)` |
| Read by id | `session.get(...)` | `em.find(...)` |
| Query language | HQL | JPQL |
| Delete | `session.remove(...)` | `em.remove(...)` |

## Switching to EclipseLink
Would change: the dependency in `pom.xml`, the `hibernate.*` properties in `persistence.xml` (replaced by EclipseLink ones), and the provider if it is declared explicitly. Would not change: the `Employee` entity annotations, `EmployeeJpaDao`, `Main`, and the JPQL, because they only use `jakarta.persistence`.
