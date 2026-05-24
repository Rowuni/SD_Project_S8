package com.healthcare.appointment.notification;

import com.healthcare.appointment.domain.model.Appointment;
import com.healthcare.appointment.domain.model.Doctor;
import com.healthcare.appointment.domain.model.Patient;

/**
 * Factory responsible for building Notification objects.
 *
 * DESIGN PATTERN - Factory
 * GRASP - Creator: Centralizes notification creation logic.
 * SOLID - SRP: Only responsible for constructing notifications.
 * SOLID - OCP: New notification types are added as new factory methods.
 * GRASP - Pure Fabrication: Not a domain concept; exists to keep creation logic
 * clean.
 */
public class NotificationFactory {

    public Notification createConfirmationNotification(Appointment appointment) {
        Patient patient = appointment.getPatient();
        String subject = "Appointment Confirmed - " + appointment.getService().getName();
        String body = String.format(
                "Dear %s, your appointment with Dr. %s for %s has been confirmed on %s.",
                patient.getName(),
                appointment.getDoctor().getName(),
                appointment.getService().getName(),
                appointment.getSlot().getStart());
        return new Notification(patient, subject, body);
    }

    public Notification createCancellationNotification(Appointment appointment) {
        Patient patient = appointment.getPatient();
        String subject = "Appointment Cancelled - " + appointment.getService().getName();
        String body = String.format(
                "Dear %s, your appointment with Dr. %s on %s has been cancelled.",
                patient.getName(),
                appointment.getDoctor().getName(),
                appointment.getSlot().getStart());
        return new Notification(patient, subject, body);
    }

    public Notification createDoctorCancellationNotification(Appointment appointment) {
        Doctor doctor = appointment.getDoctor();
        String subject = "Appointment Cancelled - " + appointment.getPatient().getName();
        String body = String.format(
                "Dr. %s, the appointment with %s on %s has been cancelled.",
                doctor.getName(),
                appointment.getPatient().getName(),
                appointment.getSlot().getStart());
        return new Notification(doctor, subject, body);
    }

    public Notification createReminderNotification(Appointment appointment) {
        Patient patient = appointment.getPatient();
        String subject = "Reminder: Upcoming Appointment Tomorrow";
        String body = String.format(
                "Dear %s, this is a reminder for your appointment with Dr. %s tomorrow at %s.",
                patient.getName(),
                appointment.getDoctor().getName(),
                appointment.getSlot().getStart());
        return new Notification(patient, subject, body);
    }

    public Notification createCompletionNotification(Appointment appointment) {
        Patient patient = appointment.getPatient();
        String subject = "Appointment Completed - Thank you";
        String body = String.format(
                "Dear %s, your consultation with Dr. %s has been marked as completed. Thank you for using our platform.",
                patient.getName(),
                appointment.getDoctor().getName());
        return new Notification(patient, subject, body);
    }
}
