package com.healthcare.appointment.domain.model;

import java.util.UUID;

/**
 * Abstract base class for all users of the system.
 *
 * SOLID — SRP: Holds only identity and profile data.
 * SOLID — LSP: Patient, Doctor, Administrator all extend this without breaking contracts.
 * GRASP — Information Expert: Knows its own role and profile data.
 */
public abstract class User {

    private final String id;
    private String name;
    private String email;
    private String passwordHash;
    private final UserRole role;

    protected User(String name, String email, String passwordHash, UserRole role) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public UserRole getRole() { return role; }

    public void updateProfile(String newName, String newEmail) {
        this.name = newName;
        this.email = newEmail;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s)", role, name, email);
    }
}
