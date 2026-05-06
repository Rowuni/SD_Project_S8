package com.healthcare.appointment.domain.model;

/**
 * Represents a patient.
 *
 * SOLID — ISP: Implements only patient-relevant interface (Schedulable).
 * GRASP — Creator: Holds patient-specific data used in appointment creation context.
 */
public class Patient extends User {

    private final String medicalRecordNumber;

    public Patient(String name, String email, String passwordHash, String medicalRecordNumber) {
        super(name, email, passwordHash, UserRole.PATIENT);
        this.medicalRecordNumber = medicalRecordNumber;
    }

    public String getMedicalRecordNumber() { return medicalRecordNumber; }
}
