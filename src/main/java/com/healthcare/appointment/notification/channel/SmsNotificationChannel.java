package com.healthcare.appointment.notification.channel;

import com.healthcare.appointment.domain.model.User;
import com.healthcare.appointment.notification.Notification;

/**
 * Simulates sending a notification via SMS.
 */
public class SmsNotificationChannel implements NotificationChannel {

    @Override
    public void send(User recipient, Notification notification) {
        System.out.printf("  [SMS]   To: %-30s | %s%n",
                recipient.getEmail(), notification.getSubject());
    }

    @Override
    public String getChannelName() { return "SMS"; }
}
