package com.healthcare.appointment.domain.model;

/**
 * Represents a system administrator.
 *
 * SOLID - ISP: Only exposes administrative capabilities.
 */
public class Administrator extends User {

    public Administrator(String name, String email, String passwordHash) {
        super(name, email, passwordHash, UserRole.ADMINISTRATOR);
    }
}
