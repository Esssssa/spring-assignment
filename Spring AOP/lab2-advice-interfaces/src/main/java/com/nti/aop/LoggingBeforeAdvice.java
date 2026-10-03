package com.nti.aop;

import org.springframework.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;
import java.util.Arrays;

public class LoggingBeforeAdvice implements MethodBeforeAdvice {

    public void before(Method method, Object[] args, Object target) {
        System.out.println("[before] " + method.getName() + " " + Arrays.toString(args));
    }
}
