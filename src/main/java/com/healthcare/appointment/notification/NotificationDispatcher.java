package com.healthcare.appointment.notification;

import com.healthcare.appointment.domain.observer.AppointmentEvent;
import com.healthcare.appointment.domain.observer.AppointmentObserver;
import com.healthcare.appointment.domain.model.Appointment;
import com.healthcare.appointment.notification.channel.NotificationChannel;

import java.util.List;

/**
 * Observer that reacts to appointment lifecycle events and routes notifications.
 *
 * DESIGN PATTERN — Observer (ConcreteObserver)
 * SOLID — SRP: Only responsible for receiving events and dispatching notifications.
 * SOLID — DIP: Depends on NotificationChannel abstraction and NotificationFactory.
 * GRASP — Pure Fabrication: Not a domain concept; created for routing notification logic.
 * GRASP — Indirection: Mediates between Appointment (event source) and concrete channels.
 */
public class NotificationDispatcher implements AppointmentObserver {

    private final NotificationFactory factory;
    private final List<NotificationChannel> channels;

    public NotificationDispatcher(NotificationFactory factory, List<NotificationChannel> channels) {
        this.factory = factory;
        this.channels = channels;
    }

    @Override
    public void onAppointmentEvent(AppointmentEvent event) {
        Appointment appointment = event.getAppointment();
        Notification notification;

        switch (event.getType()) {
            case CONFIRMED:
                notification = factory.createConfirmationNotification(appointment);
                dispatch(notification);
                break;
            case CANCELLED:
                dispatch(factory.createCancellationNotification(appointment));
                dispatch(factory.createDoctorCancellationNotification(appointment));
                break;
            case COMPLETED:
                notification = factory.createCompletionNotification(appointment);
                dispatch(notification);
                break;
            case REMINDER:
                notification = factory.createReminderNotification(appointment);
                dispatch(notification);
                break;
            default:
                break;
        }
    }

    private void dispatch(Notification notification) {
        System.out.printf("%n  [NotificationDispatcher] Dispatching: %s%n", notification.getSubject());
        for (NotificationChannel channel : channels) {
            channel.send(notification.getRecipient(), notification);
        }
    }
}
