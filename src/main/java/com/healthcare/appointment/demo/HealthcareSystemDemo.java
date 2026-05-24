package com.healthcare.appointment.demo;

import com.healthcare.appointment.config.SystemConfigurationManager;
import com.healthcare.appointment.domain.model.*;
import com.healthcare.appointment.domain.observer.AppointmentObserver;
import com.healthcare.appointment.notification.NotificationDispatcher;
import com.healthcare.appointment.notification.NotificationFactory;
import com.healthcare.appointment.notification.channel.*;
import com.healthcare.appointment.payment.PaymentProcessor;
import com.healthcare.appointment.payment.PaymentResult;
import com.healthcare.appointment.payment.strategy.CreditCardPayment;
import com.healthcare.appointment.payment.strategy.DigitalWalletPayment;
import com.healthcare.appointment.pricing.PricingCalculator;
import com.healthcare.appointment.pricing.strategy.FixedDiscountStrategy;
import com.healthcare.appointment.pricing.strategy.InsuranceDiscountStrategy;
import com.healthcare.appointment.pricing.strategy.PercentageDiscountStrategy;
import com.healthcare.appointment.service.AppointmentScheduler;
import com.healthcare.appointment.service.AvailabilityService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Main demonstration class.
 *
 * Showcases the three key interactions described in the project:
 * 1. Booking an appointment (availability check + State + Observer)
 * 2. Cancelling an appointment (State transition + Observer notification)
 * 3. Payment process (Strategy + PricingCalculator + invoice generation)
 *
 * Also demonstrates:
 * - Singleton (SystemConfigurationManager)
 * - Factory (NotificationFactory)
 * - Invalid state transition protection
 */
public class HealthcareSystemDemo {

        public static void main(String[] args) {

                separator("SYSTEM STARTUP");
                SystemConfigurationManager config = SystemConfigurationManager.getInstance();
                System.out.println(config);

                // ── Infrastructure setup ───────────────────────────────────────────
                NotificationFactory notificationFactory = new NotificationFactory();
                List<NotificationChannel> channels = Arrays.asList(
                                new EmailNotificationChannel(),
                                new SmsNotificationChannel(),
                                new InAppNotificationChannel());
                NotificationDispatcher dispatcher = new NotificationDispatcher(notificationFactory, channels);
                List<AppointmentObserver> observers = List.of(dispatcher);

                AvailabilityService availabilityService = new AvailabilityService();
                AppointmentScheduler scheduler = new AppointmentScheduler(availabilityService, observers);

                // ── Domain objects ─────────────────────────────────────────────────
                Patient alice = new Patient("Alice Martin", "alice@example.com", "hashed_pw_1", "MRN-0042");
                Doctor drSmith = new Doctor("John Smith", "smith@clinic.com", "hashed_pw_2",
                                "Cardiology", "LIC-9901");
                MedicalService cardioConsult = new MedicalService(
                                "Cardiology Consultation", "Full cardiac assessment",
                                "Cardiology", 45, 120.0);

                Clinic heartClinic = new Clinic("Heart & Health Clinic", "12 Rue de la Paix, Paris");
                heartClinic.addService(cardioConsult);

                // ── Doctor defines availability ────────────────────────────────────
                separator("DEMO 1 - Doctor defines availability");
                LocalDateTime slotStart = LocalDateTime.of(2026, 5, 20, 10, 0);
                LocalDateTime slotEnd = LocalDateTime.of(2026, 5, 20, 12, 0);
                TimeSlot availableWindow = new TimeSlot(slotStart, slotEnd);
                availabilityService.defineSlot(drSmith, availableWindow);
                System.out.println("Dr. " + drSmith.getName() + " is available: " + availableWindow);

                // ── INTERACTION 1: Book an appointment ─────────────────────────────
                separator("DEMO 2 - Book an Appointment (State: SCHEDULED -> CONFIRMED + Observer)");

                TimeSlot requestedSlot = new TimeSlot(
                                LocalDateTime.of(2026, 5, 20, 10, 0),
                                LocalDateTime.of(2026, 5, 20, 10, 45));

                System.out.println("Patient: " + alice);
                System.out.println("Service: " + cardioConsult);
                System.out.println("Requested slot: " + requestedSlot);
                System.out.println();

                Appointment appointment = scheduler.scheduleAppointment(alice, drSmith, cardioConsult, requestedSlot);
                System.out.println("\nAppointment booked: " + appointment);

                // ── INTERACTION 2: Cancel an appointment ───────────────────────────
                separator("DEMO 3 - Cancel Appointment (State: CONFIRMED -> CANCELLED + Observer)");
                System.out.println("Before cancellation: " + appointment);
                scheduler.cancelAppointment(appointment.getId());
                System.out.println("After cancellation:  " + appointment);

                // ── Invalid state transition protection ───────────────────────────
                separator("DEMO 4 - Invalid State Transition (CANCELLED -> confirm)");
                try {
                        appointment.confirm(); // must throw
                } catch (Exception e) {
                        System.out.println("Caught expected exception: " + e.getMessage());
                }

                // ── INTERACTION 3: New appointment + Payment ───────────────────────
                separator("DEMO 5 - Payment Process (Strategy + Pricing)");

                Patient bob = new Patient("Bob Dupont", "bob@example.com", "hashed_pw_3", "MRN-0099");
                TimeSlot slot2 = new TimeSlot(
                                LocalDateTime.of(2026, 5, 21, 14, 0),
                                LocalDateTime.of(2026, 5, 21, 14, 45));
                availabilityService.defineSlot(drSmith, new TimeSlot(
                                LocalDateTime.of(2026, 5, 21, 13, 0),
                                LocalDateTime.of(2026, 5, 21, 17, 0)));

                Appointment appt2 = scheduler.scheduleAppointment(bob, drSmith, cardioConsult, slot2);
                System.out.println("\nNew appointment: " + appt2);

                // Build a PricingCalculator with stacked discounts
                PricingCalculator pricingCalc = new PricingCalculator();
                pricingCalc.addStrategy(new InsuranceDiscountStrategy(0.60)); // 60% covered
                pricingCalc.addStrategy(new PercentageDiscountStrategy(10.0)); // extra 10% promo
                pricingCalc.addStrategy(new FixedDiscountStrategy(5.0)); // -5EUR fixed

                // Payment via credit card
                PaymentProcessor paymentProcessor = new PaymentProcessor(pricingCalc);
                paymentProcessor.setStrategy(new CreditCardPayment("tok_visa_4242"));

                System.out.println();
                PaymentResult result = paymentProcessor.processAppointmentPayment(appt2);
                System.out.println("\n" + paymentProcessor.generateInvoice(appt2, result));

                // ── Demo: Switching payment strategy (OCP in action) ──────────────
                separator("DEMO 6 - Switch Payment Strategy to Digital Wallet (OCP)");
                PaymentProcessor walletProcessor = new PaymentProcessor(new PricingCalculator());
                walletProcessor.setStrategy(new DigitalWalletPayment("wallet-abc-123"));

                Patient carol = new Patient("Carol Durand", "carol@example.com", "hashed_pw_4", "MRN-0200");
                TimeSlot slot3 = new TimeSlot(
                                LocalDateTime.of(2026, 5, 22, 9, 0),
                                LocalDateTime.of(2026, 5, 22, 9, 45));
                availabilityService.defineSlot(drSmith, new TimeSlot(
                                LocalDateTime.of(2026, 5, 22, 8, 0),
                                LocalDateTime.of(2026, 5, 22, 12, 0)));
                Appointment appt3 = scheduler.scheduleAppointment(carol, drSmith, cardioConsult, slot3);
                PaymentResult walletResult = walletProcessor.processAppointmentPayment(appt3);
                System.out.println(walletProcessor.generateInvoice(appt3, walletResult));

                // ── Demo: Singleton uniqueness ─────────────────────────────────────
                separator("DEMO 7 - Singleton (SystemConfigurationManager)");
                SystemConfigurationManager config2 = SystemConfigurationManager.getInstance();
                System.out.println("Same instance? " + (config == config2));
                System.out.println("System name: " + config2.getString("system.name"));

                // ── Demo: Complete an appointment ──────────────────────────────────
                separator("DEMO 8 - Complete Appointment (CONFIRMED -> COMPLETED)");
                scheduler.completeAppointment(appt2.getId());
                System.out.println("Final state: " + appt2);

                separator("DEMO COMPLETE");
        }

        private static void separator(String title) {
                System.out.println("\n" + "═".repeat(60));
                System.out.println("  " + title);
                System.out.println("═".repeat(60));
        }
}
