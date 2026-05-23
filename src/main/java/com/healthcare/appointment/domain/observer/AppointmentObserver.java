package com.healthcare.appointment.domain.observer;

/**
 * Observer interface for appointment lifecycle events.
 *
 * DESIGN PATTERN - Observer
 * SOLID - ISP: Single-method interface, clients implement only what they need.
 * SOLID - DIP: Appointment depends on this abstraction, not on concrete
 * observers.
 */
public interface AppointmentObserver {
    void onAppointmentEvent(AppointmentEvent event);
}
