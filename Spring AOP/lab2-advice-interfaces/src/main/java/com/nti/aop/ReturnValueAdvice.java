package com.nti.aop;

import org.springframework.aop.AfterReturningAdvice;

import java.lang.reflect.Method;

public class ReturnValueAdvice implements AfterReturningAdvice {

    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) {
        if (method.getName().equals("checkStock")) {
            System.out.println("[after-returning] checkStock returned " + returnValue);
        }
    }
}
