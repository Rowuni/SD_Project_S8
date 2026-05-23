package com.healthcare.appointment.payment;

import com.healthcare.appointment.domain.model.Appointment;
import com.healthcare.appointment.payment.strategy.PaymentStrategy;
import com.healthcare.appointment.pricing.PricingCalculator;

/**
 * Orchestrates the payment flow for an appointment.
 *
 * DESIGN PATTERN - Strategy (context): delegates to the injected
 * PaymentStrategy.
 * SOLID - SRP: Only handles payment recording and invoice generation.
 * SOLID - DIP: Depends on PaymentStrategy and PricingCalculator abstractions.
 * GRASP - Creator: Creates Invoice objects since it holds all needed data.
 * GRASP - Controller: Handles the payment use-case boundary.
 */
public class PaymentProcessor {

    private PaymentStrategy strategy;
    private final PricingCalculator pricingCalculator;

    public PaymentProcessor(PricingCalculator pricingCalculator) {
        this.pricingCalculator = pricingCalculator;
    }

    public void setStrategy(PaymentStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Computes the final price and processes the payment using the current
     * strategy.
     */
    public PaymentResult processAppointmentPayment(Appointment appointment) {
        if (strategy == null) {
            throw new IllegalStateException("No payment strategy selected.");
        }
        double basePrice = appointment.getService().getBaseFee();
        double finalPrice = pricingCalculator.computeFinalPrice(basePrice);

        System.out.println("\n  Pricing breakdown:");
        System.out.print(pricingCalculator.getPricingBreakdown(basePrice));

        return strategy.processPayment(finalPrice);
    }

    /**
     * Generates a simple invoice summary string.
     */
    public String generateInvoice(Appointment appointment, PaymentResult result) {
        return String.format(
                "=== INVOICE ===%n" +
                        "Patient:     %s%n" +
                        "Doctor:      Dr. %s%n" +
                        "Service:     %s%n" +
                        "Date:        %s%n" +
                        "Method:      %s%n" +
                        "Amount paid: %.2fEUR%n" +
                        "Transaction: %s%n" +
                        "Status:      %s%n" +
                        "================",
                appointment.getPatient().getName(),
                appointment.getDoctor().getName(),
                appointment.getService().getName(),
                appointment.getSlot().getStart(),
                strategy.getMethodName(),
                result.getAmountCharged(),
                result.getTransactionId(),
                result.isSuccess() ? "PAID" : "FAILED");
    }
}
