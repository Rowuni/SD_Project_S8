package com.healthcare.appointment.payment.strategy;

import com.healthcare.appointment.payment.PaymentResult;

/**
 * Strategy interface for payment methods.
 *
 * DESIGN PATTERN — Strategy
 * SOLID — OCP: New payment methods are added by implementing this interface.
 * SOLID — ISP: Clients only depend on processPayment and getMethodName.
 */
public interface PaymentStrategy {
    PaymentResult processPayment(double amount);
    String getMethodName();
}
