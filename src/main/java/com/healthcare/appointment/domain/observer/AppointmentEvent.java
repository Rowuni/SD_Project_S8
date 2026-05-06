package com.healthcare.appointment.domain.observer;

import com.healthcare.appointment.domain.model.Appointment;
import java.time.LocalDateTime;

/**
 * Immutable event object carrying the type and the affected appointment.
 *
 * DESIGN PATTERN — Observer (event object)
 * GRASP — Information Expert: carries all information observers need.
 */
public class AppointmentEvent {

    private final EventType type;
    private final Appointment appointment;
    private final LocalDateTime timestamp;

    public AppointmentEvent(EventType type, Appointment appointment) {
        this.type = type;
        this.appointment = appointment;
        this.timestamp = LocalDateTime.now();
    }

    public EventType getType() { return type; }
    public Appointment getAppointment() { return appointment; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return String.format("AppointmentEvent[%s] at %s", type, timestamp);
    }
}
