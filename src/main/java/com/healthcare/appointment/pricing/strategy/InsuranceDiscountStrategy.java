package com.healthcare.appointment.pricing.strategy;

/**
 * Applies an insurance coverage discount.
 * E.g., 70% coverage rate means the patient pays 30% of the base fee.
 */
public class InsuranceDiscountStrategy implements PricingStrategy {

    private final double coverageRate; // 0.0 to 1.0

    public InsuranceDiscountStrategy(double coverageRate) {
        if (coverageRate < 0.0 || coverageRate > 1.0) {
            throw new IllegalArgumentException("Coverage rate must be between 0.0 and 1.0.");
        }
        this.coverageRate = coverageRate;
    }

    @Override
    public double applyDiscount(double basePrice) {
        return basePrice * (1.0 - coverageRate);
    }

    @Override
    public String getDescription() {
        return String.format("Insurance coverage: %.0f%%", coverageRate * 100);
    }
}
