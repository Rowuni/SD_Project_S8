package com.healthcare.appointment.domain.state;

import com.healthcare.appointment.domain.model.Appointment;
import com.healthcare.appointment.domain.observer.AppointmentEvent;
import com.healthcare.appointment.domain.observer.EventType;

/**
 * Initial state — appointment has been requested but not yet confirmed.
 * Valid transitions: confirm(), cancel()
 */
public class ScheduledState implements AppointmentState {

    @Override
    public void confirm(Appointment appointment) {
        appointment.setState(new ConfirmedState());
        appointment.notifyObservers(new AppointmentEvent(EventType.CONFIRMED, appointment));
    }

    @Override
    public void cancel(Appointment appointment) {
        appointment.setState(new CancelledState());
        appointment.notifyObservers(new AppointmentEvent(EventType.CANCELLED, appointment));
    }

    @Override
    public void complete(Appointment appointment) {
        throw new InvalidStateTransitionException(getStateName(), "complete");
    }

    @Override
    public String getStateName() { return "SCHEDULED"; }
}
