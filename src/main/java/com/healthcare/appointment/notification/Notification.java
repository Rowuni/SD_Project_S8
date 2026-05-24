package com.healthcare.appointment.notification;

import com.healthcare.appointment.domain.model.User;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a notification message to be sent to a user.
 *
 * SOLID - SRP: Only carries notification content data.
 */
public class Notification {

    private final String id;
    private final User recipient;
    private final String subject;
    private final String body;
    private final LocalDateTime createdAt;

    public Notification(User recipient, String subject, String body) {
        this.id = UUID.randomUUID().toString();
        this.recipient = recipient;
        this.subject = subject;
        this.body = body;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public User getRecipient() {
        return recipient;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return String.format("Notification[%s] To: %s | Subject: %s",
                id.substring(0, 8), recipient.getEmail(), subject);
    }
}
