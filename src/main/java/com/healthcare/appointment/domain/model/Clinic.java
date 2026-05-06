package com.healthcare.appointment.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Represents a medical clinic.
 *
 * GRASP — Information Expert: Knows its services and doctors.
 * GRASP — Creator: Creates associations between services and doctors.
 */
public class Clinic {

    private final String id;
    private final String name;
    private final String address;
    private final List<MedicalService> services = new ArrayList<>();

    public Clinic(String name, String address) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.address = address;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }

    public void addService(MedicalService service) {
        services.add(service);
    }

    public List<MedicalService> getServices() {
        return Collections.unmodifiableList(services);
    }

    public List<MedicalService> filterBySpeciality(String speciality) {
        List<MedicalService> result = new ArrayList<>();
        for (MedicalService s : services) {
            if (s.getSpeciality().equalsIgnoreCase(speciality)) {
                result.add(s);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return String.format("Clinic[%s] @ %s (%d services)", name, address, services.size());
    }
}
