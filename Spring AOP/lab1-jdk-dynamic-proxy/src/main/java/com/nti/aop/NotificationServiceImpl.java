package com.nti.aop;

public class NotificationServiceImpl implements NotificationService {

    public void sendEmail(String to, String message) {
        System.out.println("Email sent to " + to + ": " + message);
    }

    public void sendSms(String to, String message) {
        System.out.println("SMS sent to " + to + ": " + message);
    }
}
