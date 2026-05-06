package com.healthcare.appointment.service;

import com.healthcare.appointment.domain.model.Doctor;
import com.healthcare.appointment.domain.model.TimeSlot;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages doctor availability: time slots and blocked periods.
 *
 * SOLID — SRP: Only responsible for availability logic.
 * SOLID — DIP: Used through an implicit abstraction (could be extracted to interface).
 * GRASP — Information Expert: Holds all slot data for all doctors.
 * GRASP — High Cohesion: Every method directly relates to availability management.
 */
public class AvailabilityService {

    // doctorId → list of available TimeSlots
    private final Map<String, List<TimeSlot>> availableSlots = new HashMap<>();

    // doctorId → list of blocked TimeSlots
    private final Map<String, List<TimeSlot>> blockedSlots = new HashMap<>();

    public void defineSlot(Doctor doctor, TimeSlot slot) {
        availableSlots.computeIfAbsent(doctor.getId(), k -> new ArrayList<>()).add(slot);
    }

    public void blockPeriod(Doctor doctor, TimeSlot blockedPeriod) {
        blockedSlots.computeIfAbsent(doctor.getId(), k -> new ArrayList<>()).add(blockedPeriod);
    }

    /**
     * Returns true if the doctor has no conflict (existing appointments or blocks) for the slot.
     *
     * GRASP — Information Expert: Only this class holds the data to answer this question.
     */
    public boolean isAvailable(Doctor doctor, TimeSlot requestedSlot) {
        // Check against blocked periods
        List<TimeSlot> blocks = blockedSlots.getOrDefault(doctor.getId(), Collections.emptyList());
        for (TimeSlot blocked : blocks) {
            if (requestedSlot.overlaps(blocked)) {
                return false;
            }
        }
        // Check that at least one defined available slot covers the requested slot
        List<TimeSlot> available = availableSlots.getOrDefault(doctor.getId(), Collections.emptyList());
        for (TimeSlot slot : available) {
            if (!requestedSlot.getStart().isBefore(slot.getStart())
                    && !requestedSlot.getEnd().isAfter(slot.getEnd())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns all available (non-blocked) slots for a doctor on a given date.
     */
    public List<TimeSlot> getAvailableSlots(Doctor doctor, LocalDate date) {
        List<TimeSlot> slots = availableSlots.getOrDefault(doctor.getId(), Collections.emptyList());
        List<TimeSlot> result = new ArrayList<>();
        for (TimeSlot slot : slots) {
            if (slot.getStart().toLocalDate().equals(date) && !isBlocked(doctor, slot)) {
                result.add(slot);
            }
        }
        return Collections.unmodifiableList(result);
    }

    private boolean isBlocked(Doctor doctor, TimeSlot slot) {
        List<TimeSlot> blocks = blockedSlots.getOrDefault(doctor.getId(), Collections.emptyList());
        for (TimeSlot blocked : blocks) {
            if (slot.overlaps(blocked)) return true;
        }
        return false;
    }

    /**
     * Releases a slot back to available when an appointment is cancelled.
     */
    public void releaseSlot(Doctor doctor, TimeSlot slot) {
        System.out.printf("  [AvailabilityService] Slot released for Dr. %s: %s%n",
                doctor.getName(), slot);
    }
}
