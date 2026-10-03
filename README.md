# spring assignment

all the tasks from the pdfs, one folder per task. every one is its own maven project so you can open them separately in intellij.

spring aop
- lab1-jdk-dynamic-proxy -> session 1 lab 1, proxy by hand with InvocationHandler
- lab2-advice-interfaces -> session 1 lab 2, before / after returning / throws / around advice with ProxyFactory and ProxyFactoryBean
- lab2-aspectj-annotations -> session 2 lab 2, the 5 advice annotations + my own @Cacheable and @Timed

spring boot
- session1-task-tracker-api -> the task tracker lab (in memory, @Value limits)
- session2-config-logging-actuator -> profiles, @ConfigurationProperties, logging, actuator

spring mvc
- task-mvc-jsp -> Task.txt (jsp pages, form, validation, exception handling)
- product-rest-api -> session 3 lab 3, rest api for products

spring data
- orm-lab1-hibernate-native -> day 1 lab 1
- orm-lab2-jpa-hello -> day 1 lab 2
- jpa-library-management -> day 2 tasks 1 and 2 (library system)
- spring-jdbc-lab1-datasource -> day 3 lab 1
- spring-jdbc-lab2-jdbctemplate -> day 3 lab 2
- spring-data-practice-orm-jpa -> day 3 practice exercises
- store -> Task.pdf, the final project (online store order management)

the mvc session 1 and "four components" pdfs are just demo projects and recaps, no student task in them, so no folder for those. mvc session 2 is the Task.txt one.

## running

plain java ones:

```bash
mvn compile exec:java -Dexec.mainClass=<main class>
```

boot ones: `mvn spring-boot:run`

ones with tests: `mvn test`

needs jdk 21 and maven 3.9. spring-jdbc-lab1 takes the profile as an argument, `-Dexec.args=dev` or `prod`.
