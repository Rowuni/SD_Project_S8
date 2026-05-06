package com.healthcare.appointment.domain.state;

import com.healthcare.appointment.domain.model.Appointment;

/**
 * State interface for the Appointment lifecycle.
 *
 * DESIGN PATTERN — State:
 *   Each concrete state encapsulates the valid transitions from that state.
 *   Invalid transitions throw InvalidStateTransitionException.
 *
 * SOLID — OCP: New states can be added by implementing this interface without
 *   modifying Appointment or existing state classes.
 * SOLID — ISP: All methods are relevant to appointment state management.
 */
public interface AppointmentState {
    void confirm(Appointment appointment);
    void cancel(Appointment appointment);
    void complete(Appointment appointment);
    String getStateName();
}
