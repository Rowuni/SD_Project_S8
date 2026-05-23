package com.healthcare.appointment.domain.model;

/**
 * Represents a medical doctor.
 *
 * SOLID - ISP: Implements only AvailabilityManageable, not Schedulable.
 * GRASP - Information Expert: Owns availability and schedule data.
 */
public class Doctor extends User {

    private final String speciality;
    private final String licenseNumber;

    public Doctor(String name, String email, String passwordHash,
            String speciality, String licenseNumber) {
        super(name, email, passwordHash, UserRole.DOCTOR);
        this.speciality = speciality;
        this.licenseNumber = licenseNumber;
    }

    public String getSpeciality() {
        return speciality;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }
}
