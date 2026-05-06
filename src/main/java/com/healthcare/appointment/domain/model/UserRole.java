package com.healthcare.appointment.domain.model;

/**
 * Enumerates the roles a user can hold in the system.
 * Used to enforce Role-Based Access Control (RBAC).
 */
public enum UserRole {
    PATIENT,
    DOCTOR,
    ADMINISTRATOR
}
