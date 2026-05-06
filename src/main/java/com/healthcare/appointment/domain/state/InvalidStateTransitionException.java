package com.healthcare.appointment.domain.state;

import com.healthcare.appointment.domain.model.Appointment;

/**
 * Exception thrown when an invalid state transition is attempted.
 */
public class InvalidStateTransitionException extends RuntimeException {
    public InvalidStateTransitionException(String currentState, String attemptedAction) {
        super(String.format("Cannot perform '%s' on an appointment in state '%s'.",
                attemptedAction, currentState));
    }
}
