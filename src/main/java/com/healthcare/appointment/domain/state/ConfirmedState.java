package com.healthcare.appointment.domain.state;

import com.healthcare.appointment.domain.model.Appointment;
import com.healthcare.appointment.domain.observer.AppointmentEvent;
import com.healthcare.appointment.domain.observer.EventType;

/**
 * Appointment is confirmed and awaiting the consultation.
 * Valid transitions: cancel(), complete()
 */
public class ConfirmedState implements AppointmentState {

    @Override
    public void confirm(Appointment appointment) {
        throw new InvalidStateTransitionException(getStateName(), "confirm");
    }

    @Override
    public void cancel(Appointment appointment) {
        appointment.setState(new CancelledState());
        appointment.notifyObservers(new AppointmentEvent(EventType.CANCELLED, appointment));
    }

    @Override
    public void complete(Appointment appointment) {
        appointment.setState(new CompletedState());
        appointment.notifyObservers(new AppointmentEvent(EventType.COMPLETED, appointment));
    }

    @Override
    public String getStateName() { return "CONFIRMED"; }
}
