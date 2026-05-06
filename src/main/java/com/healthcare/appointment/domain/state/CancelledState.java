package com.healthcare.appointment.domain.state;

import com.healthcare.appointment.domain.model.Appointment;

/**
 * Terminal state — appointment was cancelled.
 * No further transitions are valid.
 */
public class CancelledState implements AppointmentState {

    @Override
    public void confirm(Appointment appointment) {
        throw new InvalidStateTransitionException(getStateName(), "confirm");
    }

    @Override
    public void cancel(Appointment appointment) {
        throw new InvalidStateTransitionException(getStateName(), "cancel");
    }

    @Override
    public void complete(Appointment appointment) {
        throw new InvalidStateTransitionException(getStateName(), "complete");
    }

    @Override
    public String getStateName() { return "CANCELLED"; }
}
