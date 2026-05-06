package com.healthcare.appointment.domain.observer;

/**
 * Types of events that an Appointment can emit.
 * Used by the Observer pattern to carry context to observers.
 */
public enum EventType {
    CONFIRMED,
    CANCELLED,
    COMPLETED,
    RESCHEDULED,
    REMINDER
}
