package com.healthcare.appointment.pricing.strategy;

/**
 * Strategy interface for pricing adjustments.
 *
 * DESIGN PATTERN - Strategy
 * SOLID - OCP: New discount types only require a new implementation.
 * SOLID - ISP: Minimal interface with two focused methods.
 */
public interface PricingStrategy {
    double applyDiscount(double basePrice);

    String getDescription();
}
