# spring aop

**lab1-jdk-dynamic-proxy**

LoggingHandler wraps NotificationServiceImpl using Proxy.newProxyInstance. logs the method name + args before, the return value after, and how long the call took.

**lab2-advice-interfaces**

four advices on InventoryServiceImpl: MethodBeforeAdvice, AfterReturningAdvice, ThrowsAdvice and a MethodInterceptor for timing (try/finally so it always prints).
Main builds the proxy with ProxyFactory, AopConfig does the same thing as a bean with ProxyFactoryBean. the bonus with NameMatchMethodPointcut is at the bottom of Main, only reserveStock gets logged there.

the timing interceptor is added first so it wraps everything else, thats why its line comes out last for each call.

**lab2-aspectj-annotations**

part A is FourAdviceAspect (@Before, @AfterReturning, @AfterThrowing, @After) and then AroundAspect which does all of it in one @Around.
part B is the custom annotations, @Cacheable and @Timed, picked up with @annotation() pointcuts.
