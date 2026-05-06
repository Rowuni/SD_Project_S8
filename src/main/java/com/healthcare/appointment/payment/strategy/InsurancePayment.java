package com.healthcare.appointment.payment.strategy;

import com.healthcare.appointment.payment.PaymentResult;
import java.util.UUID;

/**
 * Simulates an insurance-covered payment.
 * The insurance covers a portion; the remaining amount is charged to the patient.
 *
 * DESIGN PATTERN — Strategy (Concrete Strategy)
 */
public class InsurancePayment implements PaymentStrategy {

    private final String insuranceId;
    private final double patientShareRate; // e.g., 0.30 means patient pays 30%

    public InsurancePayment(String insuranceId, double patientShareRate) {
        this.insuranceId = insuranceId;
        this.patientShareRate = patientShareRate;
    }

    @Override
    public PaymentResult processPayment(double amount) {
        double patientPays = Math.round(amount * patientShareRate * 100.0) / 100.0;
        String txId = "INS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        System.out.printf("  [InsurancePayment] Insurance %s covers %.0f%%, patient pays %.2f€%n",
                insuranceId, (1 - patientShareRate) * 100, patientPays);
        return new PaymentResult(true, txId, patientPays,
                String.format("Insurance payment simulated. Patient share: %.2f€", patientPays));
    }

    @Override
    public String getMethodName() { return "INSURANCE"; }
}
