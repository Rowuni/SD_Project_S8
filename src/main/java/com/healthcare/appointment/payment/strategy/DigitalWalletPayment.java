package com.healthcare.appointment.payment.strategy;

import com.healthcare.appointment.payment.PaymentResult;
import java.util.UUID;

/**
 * Simulates a digital wallet payment.
 *
 * DESIGN PATTERN — Strategy (Concrete Strategy)
 */
public class DigitalWalletPayment implements PaymentStrategy {

    private final String walletId;

    public DigitalWalletPayment(String walletId) {
        this.walletId = walletId;
    }

    @Override
    public PaymentResult processPayment(double amount) {
        String txId = "DW-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        System.out.printf("  [DigitalWalletPayment] Debiting %.2f€ from wallet %s%n", amount, walletId);
        return new PaymentResult(true, txId, amount, "Digital wallet payment simulated successfully.");
    }

    @Override
    public String getMethodName() { return "DIGITAL_WALLET"; }
}
