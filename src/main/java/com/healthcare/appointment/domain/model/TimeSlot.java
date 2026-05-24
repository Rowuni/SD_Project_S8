package com.healthcare.appointment.domain.model;

import java.time.LocalDateTime;

/**
 * Represents a time slot with a start and end.
 * Value object - immutable by design.
 *
 * GRASP - Information Expert: Knows whether it overlaps with another slot.
 */
public class TimeSlot {

    private final LocalDateTime start;
    private final LocalDateTime end;

    public TimeSlot(LocalDateTime start, LocalDateTime end) {
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("End time must be after start time.");
        }
        this.start = start;
        this.end = end;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    /**
     * Returns true if this slot overlaps with the given slot.
     * Used by AvailabilityService to detect scheduling conflicts.
     */
    public boolean overlaps(TimeSlot other) {
        return this.start.isBefore(other.end) && this.end.isAfter(other.start);
    }

    public long getDurationMinutes() {
        return java.time.Duration.between(start, end).toMinutes();
    }

    @Override
    public String toString() {
        return String.format("%s -> %s", start, end);
    }
}
