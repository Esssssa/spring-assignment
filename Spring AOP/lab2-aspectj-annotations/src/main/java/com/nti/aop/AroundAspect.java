package com.nti.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AroundAspect {

    @Around("execution(* com.nti.aop.AccountService.*(..))")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        String name = joinPoint.getSignature().getName();
        System.out.println("@Around before " + name);
        try {
            Object result = joinPoint.proceed();
            System.out.println("@Around returned " + result);
            return result;
        } catch (Throwable ex) {
            System.out.println("@Around threw " + ex.getMessage());
            throw ex;
        } finally {
            System.out.println("@Around finally " + name);
        }
    }
}
