package com.healthcare.appointment.notification.channel;

import com.healthcare.appointment.domain.model.User;
import com.healthcare.appointment.notification.Notification;

/**
 * Simulates delivering an in-application notification.
 */
public class InAppNotificationChannel implements NotificationChannel {

    @Override
    public void send(User recipient, Notification notification) {
        System.out.printf("  [IN-APP] Bell notification for %-25s | %s%n",
                recipient.getName(), notification.getSubject());
    }

    @Override
    public String getChannelName() { return "IN_APP"; }
}
