package com.healthcare.appointment.service;

import com.healthcare.appointment.domain.model.Appointment;
import com.healthcare.appointment.domain.model.Doctor;
import com.healthcare.appointment.domain.model.MedicalService;
import com.healthcare.appointment.domain.model.Patient;
import com.healthcare.appointment.domain.model.TimeSlot;
import com.healthcare.appointment.domain.observer.AppointmentObserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Application-layer service that orchestrates the appointment booking lifecycle.
 *
 * DESIGN PATTERN — uses State (via Appointment) and Observer (via registered observers).
 * SOLID — SRP: Only orchestrates; delegates availability checks, notifications, pricing.
 * SOLID — DIP: Depends on AvailabilityService and observers via abstractions.
 * GRASP — Controller: Entry point for appointment use-case logic.
 * GRASP — Creator: Constructs Appointment objects (holds all data needed).
 * GRASP — Low Coupling: Does not reference concrete notification or payment classes.
 */
public class AppointmentScheduler {

    private final AvailabilityService availabilityService;
    private final List<AppointmentObserver> defaultObservers;

    // Simple in-memory store for the prototype
    private final Map<String, Appointment> appointments = new HashMap<>();

    public AppointmentScheduler(AvailabilityService availabilityService,
                                List<AppointmentObserver> defaultObservers) {
        this.availabilityService = availabilityService;
        this.defaultObservers = defaultObservers;
    }

    /**
     * Books a new appointment after verifying doctor availability.
     *
     * @throws IllegalStateException if the doctor is not available for the requested slot.
     */
    public Appointment scheduleAppointment(Patient patient, Doctor doctor,
                                           MedicalService service, TimeSlot slot) {
        if (!availabilityService.isAvailable(doctor, slot)) {
            throw new IllegalStateException(
                    "Doctor " + doctor.getName() + " is not available for slot: " + slot);
        }

        Appointment appointment = new Appointment(patient, doctor, service, slot);

        // Register all default observers (e.g., NotificationDispatcher)
        for (AppointmentObserver observer : defaultObservers) {
            appointment.subscribe(observer);
        }

        appointment.confirm(); // triggers State transition + Observer notification

        appointments.put(appointment.getId(), appointment);
        return appointment;
    }

    /**
     * Cancels an existing appointment by ID.
     */
    public void cancelAppointment(String appointmentId) {
        Appointment appointment = findById(appointmentId);
        appointment.cancel(); // triggers State transition + Observer notification
        availabilityService.releaseSlot(appointment.getDoctor(), appointment.getSlot());
    }

    /**
     * Marks an appointment as completed (called after consultation).
     */
    public void completeAppointment(String appointmentId) {
        Appointment appointment = findById(appointmentId);
        appointment.complete();
    }

    public Appointment findById(String appointmentId) {
        Appointment appointment = appointments.get(appointmentId);
        if (appointment == null) {
            throw new IllegalArgumentException("No appointment found with id: " + appointmentId);
        }
        return appointment;
    }

    public List<Appointment> getAllAppointments() {
        return new ArrayList<>(appointments.values());
    }
}
