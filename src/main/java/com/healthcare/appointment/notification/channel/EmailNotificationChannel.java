package com.healthcare.appointment.notification.channel;

import com.healthcare.appointment.domain.model.User;
import com.healthcare.appointment.notification.Notification;

/**
 * Simulates sending a notification via email.
 *
 * DESIGN PATTERN — Strategy / Observer (concrete observer channel)
 * SOLID — LSP: Can replace any NotificationChannel without side effects.
 */
public class EmailNotificationChannel implements NotificationChannel {

    @Override
    public void send(User recipient, Notification notification) {
        System.out.printf("  [EMAIL] To: %-30s | Subject: %s%n",
                recipient.getEmail(), notification.getSubject());
        System.out.printf("          Body: %s%n", notification.getBody());
    }

    @Override
    public String getChannelName() { return "EMAIL"; }
}
