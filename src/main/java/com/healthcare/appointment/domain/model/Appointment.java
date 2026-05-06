package com.healthcare.appointment.domain.model;

import com.healthcare.appointment.domain.observer.AppointmentEvent;
import com.healthcare.appointment.domain.observer.AppointmentObserver;
import com.healthcare.appointment.domain.state.AppointmentState;
import com.healthcare.appointment.domain.state.ScheduledState;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Core domain entity representing a medical appointment.
 *
 * DESIGN PATTERN — State: delegates all lifecycle transitions to the current AppointmentState.
 * DESIGN PATTERN — Observer (Subject): notifies registered observers on every state change.
 *
 * SOLID — SRP: Only holds appointment data and delegates transitions/notifications.
 * SOLID — OCP: New states are added without modifying this class.
 * GRASP — Information Expert: Knows its patient, doctor, service, slot and current state.
 */
public class Appointment {

    private final String id;
    private final Patient patient;
    private final Doctor doctor;
    private final MedicalService service;
    private final TimeSlot slot;

    private AppointmentState state;
    private final List<AppointmentObserver> observers = new ArrayList<>();

    public Appointment(Patient patient, Doctor doctor, MedicalService service, TimeSlot slot) {
        this.id = UUID.randomUUID().toString();
        this.patient = patient;
        this.doctor = doctor;
        this.service = service;
        this.slot = slot;
        this.state = new ScheduledState();  // initial state
    }

    // ── State Pattern delegation ──────────────────────────────────────────────

    public void confirm() {
        state.confirm(this);
    }

    public void cancel() {
        state.cancel(this);
    }

    public void complete() {
        state.complete(this);
    }

    /** Called by state classes during transitions — package-accessible. */
    public void setState(AppointmentState newState) {
        this.state = newState;
    }

    public String getStateName() {
        return state.getStateName();
    }

    // ── Observer Pattern ──────────────────────────────────────────────────────

    public void subscribe(AppointmentObserver observer) {
        observers.add(observer);
    }

    public void unsubscribe(AppointmentObserver observer) {
        observers.remove(observer);
    }

    /** Called by state classes — notifies all registered observers. */
    public void notifyObservers(AppointmentEvent event) {
        for (AppointmentObserver observer : observers) {
            observer.onAppointmentEvent(event);
        }
    }

    // ── Accessors ─────────────────────────────────────────────────────────────

    public String getId() { return id; }
    public Patient getPatient() { return patient; }
    public Doctor getDoctor() { return doctor; }
    public MedicalService getService() { return service; }
    public TimeSlot getSlot() { return slot; }

    @Override
    public String toString() {
        return String.format("Appointment[%s] %s with Dr. %s — %s [%s]",
                id.substring(0, 8), service.getName(), doctor.getName(),
                slot, state.getStateName());
    }
}
