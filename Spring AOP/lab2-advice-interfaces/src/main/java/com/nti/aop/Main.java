package com.nti.aop;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {

    public static void main(String[] args) {
        System.out.println("=== ProxyFactory with all four advices ===");
        ProxyFactory factory = new ProxyFactory(new InventoryServiceImpl());
        factory.addAdvice(new TimingInterceptor());
        factory.addAdvice(new LoggingBeforeAdvice());
        factory.addAdvice(new ReturnValueAdvice());
        factory.addAdvice(new ExceptionLoggingAdvice());
        InventoryService service = (InventoryService) factory.getProxy();

        service.checkStock("SKU-1");
        service.reserveStock("SKU-1", 5);
        try {
            service.reserveStock("SKU-1", 500);
        } catch (IllegalStateException e) {
            System.out.println("caught: " + e.getMessage());
        }

        System.out.println();
        System.out.println("=== Spring container with ProxyFactoryBean ===");
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AopConfig.class)) {
            InventoryService bean = context.getBean("inventoryService", InventoryService.class);
            System.out.println("proxy class: " + bean.getClass().getName());
            bean.checkStock("SKU-2");
            bean.reserveStock("SKU-2", 10);
        }

        System.out.println();
        System.out.println("=== NameMatchMethodPointcut: only reserveStock is logged ===");
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.addMethodName("reserveStock");
        ProxyFactory selective = new ProxyFactory(new InventoryServiceImpl());
        selective.addAdvisor(new DefaultPointcutAdvisor(pointcut, new LoggingBeforeAdvice()));
        InventoryService filtered = (InventoryService) selective.getProxy();
        filtered.checkStock("SKU-3");
        filtered.reserveStock("SKU-3", 7);
    }
}
