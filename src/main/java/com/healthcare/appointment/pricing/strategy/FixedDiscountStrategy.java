package com.healthcare.appointment.pricing.strategy;

/**
 * Applies a fixed monetary reduction to the base price.
 * E.g., -15€.
 */
public class FixedDiscountStrategy implements PricingStrategy {

    private final double discountAmount;

    public FixedDiscountStrategy(double discountAmount) {
        if (discountAmount < 0) {
            throw new IllegalArgumentException("Discount amount cannot be negative.");
        }
        this.discountAmount = discountAmount;
    }

    @Override
    public double applyDiscount(double basePrice) {
        return Math.max(0.0, basePrice - discountAmount);
    }

    @Override
    public String getDescription() {
        return String.format("Fixed discount: -%.2f€", discountAmount);
    }
}
