# Spring AOP

## lab1-jdk-dynamic-proxy
`LoggingHandler` wraps `NotificationServiceImpl` through `Proxy.newProxyInstance`. Each call logs the method name and arguments, the return value, and the elapsed time.

## lab2-advice-interfaces
Four advices on `InventoryServiceImpl`: `MethodBeforeAdvice`, `AfterReturningAdvice`, `ThrowsAdvice` and a `MethodInterceptor` that times every call with `try/finally`.

- `Main` builds the proxy with `ProxyFactory` and shows each advice firing.
- `AopConfig` exposes the same proxy as a container bean with `ProxyFactoryBean`.
- The bonus uses `NameMatchMethodPointcut` so that only `reserveStock` is logged.

The interceptor is listed first so it wraps the other advices, which is why its timing line prints last for each call.

## lab2-aspectj-annotations
- Part A: `FourAdviceAspect` uses `@Before`, `@AfterReturning`, `@AfterThrowing` and `@After`. `AroundAspect` replaces all four with one `@Around`.
- Part B: `@Cacheable` and `@Timed` are custom annotations picked up with `@annotation(...)` pointcuts by `CacheAspect` and `TimingAspect`.
