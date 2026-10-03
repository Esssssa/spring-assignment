package com.nti.aop;

import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AopConfig {

    @Bean
    public InventoryServiceImpl inventoryTarget() {
        return new InventoryServiceImpl();
    }

    @Bean
    public LoggingBeforeAdvice loggingBeforeAdvice() {
        return new LoggingBeforeAdvice();
    }

    @Bean
    public ReturnValueAdvice returnValueAdvice() {
        return new ReturnValueAdvice();
    }

    @Bean
    public ExceptionLoggingAdvice exceptionLoggingAdvice() {
        return new ExceptionLoggingAdvice();
    }

    @Bean
    public TimingInterceptor timingInterceptor() {
        return new TimingInterceptor();
    }

    @Bean
    public ProxyFactoryBean inventoryService() {
        ProxyFactoryBean factory = new ProxyFactoryBean();
        factory.setTarget(inventoryTarget());
        factory.setInterfaces(InventoryService.class);
        factory.setInterceptorNames("timingInterceptor", "loggingBeforeAdvice", "returnValueAdvice", "exceptionLoggingAdvice");
        return factory;
    }
}
