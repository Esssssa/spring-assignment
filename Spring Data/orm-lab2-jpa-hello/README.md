# orm day 1 - lab 2 (jpa)

same Employee class as lab 1 but configured with META-INF/persistence.xml. run com.training.Main.

| | hibernate native | jpa |
|---|---|---|
| bootstrap | SessionFactory | EntityManagerFactory |
| unit of work | Session | EntityManager |
| config | hibernate.cfg.xml | persistence.xml |
| create | session.persist | em.persist |
| read by id | session.get | em.find |
| query language | HQL | JPQL |
| delete | session.remove | em.remove |

**what if we switch to eclipselink?**

what changes: the dependency in the pom, and the hibernate.* properties in persistence.xml (eclipselink has its own ones).
what doesnt: Employee, EmployeeJpaDao, Main and the jpql, since they only use jakarta.persistence.
