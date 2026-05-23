package com.healthcare.appointment.payment;

/**
 * Value object representing the result of a payment operation.
 * Immutable.
 */
public class PaymentResult {

    private final boolean success;
    private final String transactionId;
    private final double amountCharged;
    private final String message;

    public PaymentResult(boolean success, String transactionId,
            double amountCharged, String message) {
        this.success = success;
        this.transactionId = transactionId;
        this.amountCharged = amountCharged;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public double getAmountCharged() {
        return amountCharged;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return String.format("PaymentResult[success=%b, txId=%s, amount=%.2fEUR, msg=%s]",
                success, transactionId, amountCharged, message);
    }
}
