package com.healthcare.appointment.notification.channel;

import com.healthcare.appointment.domain.model.User;
import com.healthcare.appointment.notification.Notification;

/**
 * Abstraction for notification delivery channels.
 *
 * DESIGN PATTERN — Strategy (for channel selection)
 * SOLID — OCP: New channels (e.g., push) are added by implementing this interface.
 * SOLID — ISP: Minimal two-method interface.
 * SOLID — DIP: NotificationDispatcher depends on this abstraction.
 */
public interface NotificationChannel {
    void send(User recipient, Notification notification);
    String getChannelName();
}
