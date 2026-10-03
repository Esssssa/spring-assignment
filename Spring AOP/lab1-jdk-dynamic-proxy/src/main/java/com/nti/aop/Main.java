package com.nti.aop;

import java.lang.reflect.Proxy;

public class Main {

    public static void main(String[] args) {
        NotificationService target = new NotificationServiceImpl();
        NotificationService proxy = (NotificationService) Proxy.newProxyInstance(
                NotificationService.class.getClassLoader(),
                new Class<?>[]{NotificationService.class},
                new LoggingHandler(target));

        proxy.sendEmail("ahmed@example.com", "Welcome aboard");
        proxy.sendSms("01012345678", "Your code is 4821");
    }
}
