package com.nti.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class FourAdviceAspect {

    @Pointcut("execution(* com.nti.aop.AccountService.*(..))")
    public void accountMethods() {
    }

    @Before("accountMethods()")
    public void before(JoinPoint joinPoint) {
        System.out.println("@Before " + joinPoint.getSignature().getName());
    }

    @AfterReturning(pointcut = "accountMethods()", returning = "result")
    public void afterReturning(JoinPoint joinPoint, Object result) {
        System.out.println("@AfterReturning " + joinPoint.getSignature().getName() + " -> " + result);
    }

    @AfterThrowing(pointcut = "accountMethods()", throwing = "ex")
    public void afterThrowing(JoinPoint joinPoint, Exception ex) {
        System.out.println("@AfterThrowing " + joinPoint.getSignature().getName() + " -> " + ex.getMessage());
    }

    @After("accountMethods()")
    public void after(JoinPoint joinPoint) {
        System.out.println("@After " + joinPoint.getSignature().getName());
    }
}
