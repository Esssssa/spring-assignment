package com.nti.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class CacheAspect {

    private final Map<String, Object> cache = new HashMap<>();

    @Around("@annotation(com.nti.aop.Cacheable)")
    public Object cache(ProceedingJoinPoint joinPoint) throws Throwable {
        String key = joinPoint.getSignature().toShortString() + Arrays.toString(joinPoint.getArgs());
        if (cache.containsKey(key)) {
            System.out.println("Cache hit for " + key);
            return cache.get(key);
        }
        Object result = joinPoint.proceed();
        cache.put(key, result);
        return result;
    }
}
