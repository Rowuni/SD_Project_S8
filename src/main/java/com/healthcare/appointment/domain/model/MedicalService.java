package com.healthcare.appointment.domain.model;

import java.util.UUID;

/**
 * Represents a medical service offered by a clinic.
 *
 * GRASP - Information Expert: Holds the base fee and service metadata.
 */
public class MedicalService {

    private final String id;
    private final String name;
    private final String description;
    private final String speciality;
    private final int durationMinutes;
    private final double baseFee;

    public MedicalService(String name, String description, String speciality,
            int durationMinutes, double baseFee) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.speciality = speciality;
        this.durationMinutes = durationMinutes;
        this.baseFee = baseFee;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getSpeciality() {
        return speciality;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public double getBaseFee() {
        return baseFee;
    }

    @Override
    public String toString() {
        return String.format("%s [%s] - %.2fEUR / %dmin", name, speciality, baseFee, durationMinutes);
    }
}
