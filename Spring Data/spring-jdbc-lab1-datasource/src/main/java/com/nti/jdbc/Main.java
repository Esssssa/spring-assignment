package com.nti.jdbc;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.sql.DataSource;

public class Main {

    public static void main(String[] args) {
        String profile = args.length > 0 ? args[0] : "dev";
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.getEnvironment().setActiveProfiles(profile);
            context.register(DataSourceConfig.class);
            context.refresh();

            PlainEmployeeDao dao = context.getBean(PlainEmployeeDao.class);
            System.out.println("Profile: " + profile);
            dao.findAll().forEach(System.out::println);
            System.out.println("Employee 3: " + dao.findById(3));
            System.out.println("Count: " + dao.count());

            DataSource ds = context.getBean(DataSource.class);
            System.out.println("DataSource class: " + ds.getClass());
        }
    }
}
