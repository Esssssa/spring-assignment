package com.nti.aop;

import org.springframework.aop.ThrowsAdvice;

import java.lang.reflect.Method;

public class ExceptionLoggingAdvice implements ThrowsAdvice {

    public void afterThrowing(Method method, Object[] args, Object target, Exception ex) {
        if (method.getName().equals("reserveStock")) {
            System.out.println("[throws] " + method.getName() + " failed: " + ex.getMessage());
        }
    }
}
