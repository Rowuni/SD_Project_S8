package com.healthcare.appointment.payment.strategy;

import com.healthcare.appointment.payment.PaymentResult;
import java.util.UUID;

/**
 * Simulates a credit card payment.
 *
 * DESIGN PATTERN — Strategy (Concrete Strategy)
 * SOLID — LSP: Substitutable for any other PaymentStrategy without breaking PaymentProcessor.
 */
public class CreditCardPayment implements PaymentStrategy {

    private final String cardToken;

    public CreditCardPayment(String cardToken) {
        this.cardToken = cardToken;
    }

    @Override
    public PaymentResult processPayment(double amount) {
        // Simulation — no real transaction occurs
        String txId = "CC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        System.out.printf("  [CreditCardPayment] Charging %.2f€ to card ending **** (token: %s)%n",
                amount, cardToken);
        return new PaymentResult(true, txId, amount, "Credit card payment simulated successfully.");
    }

    @Override
    public String getMethodName() { return "CREDIT_CARD"; }
}
