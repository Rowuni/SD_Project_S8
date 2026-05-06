package com.healthcare.appointment.pricing.strategy;

/**
 * Applies a percentage-based promotional discount.
 * E.g., 20% off.
 */
public class PercentageDiscountStrategy implements PricingStrategy {

    private final double discountPercent; // 0.0 to 100.0

    public PercentageDiscountStrategy(double discountPercent) {
        if (discountPercent < 0.0 || discountPercent > 100.0) {
            throw new IllegalArgumentException("Discount percent must be between 0 and 100.");
        }
        this.discountPercent = discountPercent;
    }

    @Override
    public double applyDiscount(double basePrice) {
        return basePrice * (1.0 - discountPercent / 100.0);
    }

    @Override
    public String getDescription() {
        return String.format("Promotional discount: %.0f%%", discountPercent);
    }
}
