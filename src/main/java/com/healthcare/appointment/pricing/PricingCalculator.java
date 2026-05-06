package com.healthcare.appointment.pricing;

import com.healthcare.appointment.pricing.strategy.PricingStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Computes the final price of a medical service by applying a chain of PricingStrategies.
 *
 * DESIGN PATTERN — Strategy (context): iterates over a list of strategies.
 * SOLID — SRP: Only responsible for price computation.
 * SOLID — OCP: Strategies are injected; new discount types never modify this class.
 * SOLID — DIP: Depends on PricingStrategy abstraction.
 * GRASP — Information Expert: Holds all pricing strategies needed to compute the final cost.
 * GRASP — Pure Fabrication: Not a domain concept; created to give discount computation a clean home.
 */
public class PricingCalculator {

    private final List<PricingStrategy> strategies = new ArrayList<>();

    public void addStrategy(PricingStrategy strategy) {
        strategies.add(strategy);
    }

    /**
     * Applies all registered strategies sequentially and returns the final price.
     */
    public double computeFinalPrice(double basePrice) {
        double price = basePrice;
        for (PricingStrategy strategy : strategies) {
            price = strategy.applyDiscount(price);
        }
        return Math.round(price * 100.0) / 100.0;
    }

    /**
     * Returns a human-readable breakdown of how the final price was reached.
     */
    public String getPricingBreakdown(double basePrice) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("  Base price:        %.2f€%n", basePrice));
        double price = basePrice;
        for (PricingStrategy strategy : strategies) {
            double before = price;
            price = strategy.applyDiscount(price);
            sb.append(String.format("  %-30s %.2f€ → %.2f€%n",
                    strategy.getDescription() + ":", before, price));
        }
        sb.append(String.format("  ─────────────────────────────────%n"));
        sb.append(String.format("  Final price:       %.2f€%n", Math.round(price * 100.0) / 100.0));
        return sb.toString();
    }

    public List<PricingStrategy> getStrategies() {
        return Collections.unmodifiableList(strategies);
    }
}
