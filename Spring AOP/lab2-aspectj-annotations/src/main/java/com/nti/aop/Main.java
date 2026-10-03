package com.nti.aop;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Part A: four separate advices ===");
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(AppConfig.class, FourAdviceAspect.class);
            context.refresh();
            runAccount(context.getBean(AccountService.class));
        }

        System.out.println();
        System.out.println("=== Part A: single around advice ===");
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(AppConfig.class, AroundAspect.class);
            context.refresh();
            runAccount(context.getBean(AccountService.class));
        }

        System.out.println();
        System.out.println("=== Part B: custom @Cacheable and @Timed ===");
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            ProductService products = context.getBean(ProductService.class);
            System.out.println(products.findProduct(7));
            System.out.println(products.findProduct(7));
            System.out.println(products.findProduct(8));
            products.rebuildIndex();
        }
    }

    private static void runAccount(AccountService accounts) {
        accounts.getBalance("ACC-1");
        try {
            accounts.withdraw("ACC-1", 5000);
        } catch (IllegalArgumentException e) {
            System.out.println("caught: " + e.getMessage());
        }
    }
}
