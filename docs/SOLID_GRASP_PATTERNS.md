# Design Principles & Patterns — Healthcare Appointment Management System

> This document serves as the detailed reference for the written report.  
> It covers SOLID principles, GRASP principles, and the applied design patterns, with justifications rooted in the system's architecture.

---

## Table of Contents

1. [SOLID Principles](#1-solid-principles)
2. [GRASP Principles](#2-grasp-principles)
3. [Design Patterns](#3-design-patterns)

---

## 1. SOLID Principles

### 1.1 Single Responsibility Principle (SRP)

> *"A class should have only one reason to change."*

Every class in the system is scoped to a single concern. Responsibilities that might be tempted to live together are deliberately separated into dedicated classes.

**Application in our system:**

| Class / Component | Single Responsibility |
|---|---|
| `AppointmentScheduler` | Orchestrates the booking flow; delegates availability checks and persistence |
| `AvailabilityService` | Solely responsible for verifying and managing doctor time slots |
| `NotificationDispatcher` | Dispatches notifications; does not decide content or channel |
| `PricingCalculator` | Computes final cost based on base price and applicable discounts |
| `PaymentProcessor` | Records payment method selection and simulates confirmation |
| `UserProfileService` | Manages personal profile data; authentication is handled separately |
| `AuthService` | Handles registration, login and token management only |

**Concrete justification:**  
Consider `AppointmentScheduler`: it only orchestrates the booking lifecycle. It does **not** check doctor availability itself (delegated to `AvailabilityService`), does **not** send notifications (delegated to `NotificationDispatcher`), and does **not** compute prices (delegated to `PricingCalculator`). Each of these could change independently — e.g., adding SMS notifications does not require touching the appointment scheduling logic.

```java
// AppointmentScheduler.java — single responsibility: orchestrate the booking flow
public Appointment scheduleAppointment(Patient patient, Doctor doctor,
                                       MedicalService service, TimeSlot slot) {
    if (!availabilityService.isAvailable(doctor, slot)) {       // delegate check
        throw new IllegalStateException("Doctor not available");
    }
    Appointment appointment = new Appointment(patient, doctor, service, slot);
    for (AppointmentObserver observer : defaultObservers) {
        appointment.subscribe(observer);                         // delegate notification
    }
    appointment.confirm();                                       // triggers State + Observer
    appointments.put(appointment.getId(), appointment);
    return appointment;
}
```

---

### 1.2 Open/Closed Principle (OCP)

> *"Software entities should be open for extension, but closed for modification."*

The system is designed so that new capabilities (new payment methods, notification channels, discount types) can be added by writing **new classes**, without modifying existing ones.

**Application in our system:**

- **Payment methods:** The `PaymentStrategy` interface defines the contract. Adding a `CryptoWalletPayment` class in the future only requires implementing this interface — `PaymentProcessor` does not change.
- **Pricing adjustments:** The `PricingStrategy` interface (with implementations `InsuranceDiscount`, `PercentageDiscount`, `FixedDiscount`) allows stacking discount strategies. New discount types are extensions, not modifications.
- **Notification channels:** The `NotificationChannel` interface allows adding a `PushNotificationChannel` without modifying `NotificationDispatcher`.
- **Appointment state transitions:** The `AppointmentState` interface encapsulates transition logic per state. Adding a new state (e.g., `PENDING_PAYMENT`) is done by creating a new class.

---

### 1.3 Liskov Substitution Principle (LSP)

> *"Objects of a subtype must be substitutable for objects of their supertype without altering program correctness."*

All abstractions in the system are designed so that any implementation can replace another without breaking the consuming code.

**Application in our system:**

- `CreditCardPayment`, `InsurancePayment`, and `DigitalWalletPayment` all implement `PaymentStrategy`. The `PaymentProcessor` calls `processPayment(amount)` on any of them and receives a consistent result.
- `EmailNotificationChannel`, `SmsNotificationChannel`, and `InAppNotificationChannel` all implement `NotificationChannel`. The dispatcher iterates over a list of channels; replacing one never breaks the dispatch logic.
- `Patient`, `Doctor`, and `Administrator` all extend `User`. Any method accepting a `User` (e.g., profile update) works correctly regardless of the concrete subtype.

**Invariants maintained:**  
Every `PaymentStrategy` implementation always returns a `PaymentResult` with a non-null status. Every `NotificationChannel` implementation always either sends successfully or throws a typed `NotificationException`, never silently fails.

```java
// PaymentProcessor uses any PaymentStrategy — CreditCard, Insurance, DigitalWallet all work
public PaymentResult processAppointmentPayment(Appointment appointment) {
    double finalPrice = pricingCalculator.computeFinalPrice(appointment.getService().getBaseFee());
    return strategy.processPayment(finalPrice);  // identical call regardless of implementation
}

// LSP in action — CreditCardPayment and DigitalWalletPayment are fully substitutable:
paymentProcessor.setStrategy(new CreditCardPayment("tok_visa_4242"));
PaymentResult r1 = paymentProcessor.processAppointmentPayment(appt);  // works

paymentProcessor.setStrategy(new DigitalWalletPayment("wallet-abc-123"));
PaymentResult r2 = paymentProcessor.processAppointmentPayment(appt);  // works identically
```

---

### 1.4 Interface Segregation Principle (ISP)

> *"Clients should not be forced to depend on interfaces they do not use."*

Rather than one large `UserCapabilities` interface, the system defines fine-grained interfaces aligned with actor roles.

**Application in our system:**

| Interface | Methods | Who implements it |
|---|---|---|
| `Schedulable` | `requestAppointment()`, `cancelAppointment()`, `viewAppointmentHistory()` | `Patient` |
| `AvailabilityManageable` | `defineAvailability()`, `blockPeriod()`, `viewSchedule()` | `Doctor` |
| `SystemAdministrable` | `manageUsers()`, `manageClinics()`, `manageServices()` | `Administrator` |
| `Notifiable` | `receiveNotification(Notification)` | `Patient`, `Doctor` |
| `PaymentStrategy` | `processPayment(amount)`, `getMethodName()` | Payment implementations |
| `PricingStrategy` | `applyDiscount(basePrice)`, `getDescription()` | Discount implementations |
| `NotificationChannel` | `send(recipient, message)` | Channel implementations |

A `Doctor` is never forced to implement `requestAppointment()`, and a payment strategy class is never burdened with availability management methods.

```java
// Fine-grained interfaces — each actor only sees what it needs

public interface PaymentStrategy {
    PaymentResult processPayment(double amount);  // payment classes only
    String getMethodName();
}

public interface NotificationChannel {
    void send(User recipient, Notification notification);  // channel classes only
    String getChannelName();
}

// AppointmentObserver — single focused method
public interface AppointmentObserver {
    void onAppointmentEvent(AppointmentEvent event);
}
// NotificationDispatcher implements ONLY AppointmentObserver — not a full "UserManager" god interface
```

---

### 1.5 Dependency Inversion Principle (DIP)

> *"High-level modules should not depend on low-level modules. Both should depend on abstractions."*

All service classes declare their dependencies through interfaces, injected at construction time (constructor injection).

**Application in our system:**

- `AppointmentScheduler` depends on `IAvailabilityService`, `INotificationDispatcher`, `IPricingCalculator`, and `IAppointmentRepository` — all interfaces. The concrete implementations are injected externally.
- `NotificationDispatcher` depends on `List<NotificationChannel>` — it does not know whether it is sending emails or SMS.
- `PaymentProcessor` depends on `PaymentStrategy` — it does not know whether the user is paying by card or insurance.
- `PricingCalculator` depends on `List<PricingStrategy>` — new discount rules can be injected without changing the calculator.

This makes every high-level service fully **unit-testable** in isolation by injecting mock implementations.

```java
// AppointmentScheduler constructor — all dependencies are abstractions
public AppointmentScheduler(AvailabilityService availabilityService,
                            List<AppointmentObserver> defaultObservers) {
    this.availabilityService = availabilityService;  // could be a mock in tests
    this.defaultObservers = defaultObservers;        // could include a test spy
}

// NotificationDispatcher — does not know about Email or SMS concretely
public class NotificationDispatcher implements AppointmentObserver {
    private final NotificationFactory factory;
    private final List<NotificationChannel> channels;  // injected: email, SMS, in-app

    public NotificationDispatcher(NotificationFactory factory,
                                  List<NotificationChannel> channels) {
        this.factory = factory;
        this.channels = channels;
    }
}
```

---

## 2. GRASP Principles

### 2.1 Creator

> *"Assign the responsibility of creating an object to the class that aggregates, contains, or closely uses it."*

| Object to create | Creator | Justification |
|---|---|---|
| `Appointment` | `AppointmentScheduler` | It orchestrates all data needed to build an appointment |
| `Notification` | `NotificationFactory` | Centralizes creation logic for all notification types |
| `Invoice` | `PaymentProcessor` | It holds all pricing and payment data required |
| `TimeSlot` | `Doctor` (via `AvailabilityService`) | The doctor owns availability data |

The `NotificationFactory` is specifically introduced to respect both the Creator principle and the Factory pattern: notifications require conditional construction (type, channel, recipient) which warrants a dedicated creator.

---

### 2.2 Information Expert

> *"Assign responsibility to the class that has the information needed to fulfill it."*

| Responsibility | Assigned to | Information held |
|---|---|---|
| Check if a time slot is available | `AvailabilityService` | All doctor time slots and existing appointments |
| Compute appointment cost | `PricingCalculator` | Base service price + applicable strategies |
| Know appointment current state | `Appointment` | Its own `AppointmentState` |
| Know user notification preferences | `UserPreferences` | Channel preferences per event type |
| Know which doctors serve a clinic | `Clinic` | Its list of `MedicalService` and assigned `Doctor` |

---

### 2.3 Controller

> *"Assign the responsibility of handling system events to a class representing the overall system or a use-case scenario."*

The system uses **use-case controllers** rather than one monolithic controller:

| Controller | Use Case Handled |
|---|---|
| `AppointmentController` | Request, modify, cancel appointment |
| `AvailabilityController` | Define time slots, block periods |
| `UserController` | Register, login, update profile |
| `CatalogController` | Browse and filter clinics/services |
| `PaymentController` | Select payment method, confirm payment |

Each controller receives requests from the outside (UI, API), validates inputs, and delegates to the appropriate service layer. Controllers do **not** contain business logic.

---

### 2.4 Low Coupling

> *"Assign responsibilities to minimize dependencies between classes."*

- Services communicate through **interfaces**, not concrete classes.
- `AppointmentScheduler` does not know about `EmailNotificationChannel` directly; it only knows `INotificationDispatcher`.
- `PricingCalculator` is completely independent of `PaymentProcessor`; it receives a `double basePrice` and returns a `double finalPrice`.
- Domain model objects (`Appointment`, `Doctor`, `Patient`) do **not** reference service classes, only value objects and other domain entities.

---

### 2.5 High Cohesion

> *"Keep objects appropriately focused, understandable, and manageable."*

Each class has a small, well-defined set of responsibilities that logically belong together:

- `Appointment` only holds appointment data (patient, doctor, service, time, state) and transitions.
- `AvailabilityService` only manages availability logic.
- `NotificationDispatcher` only manages the routing and dispatching of notifications.
- `Clinic` only aggregates services and location data.

Classes with multiple unrelated responsibilities were split: for example, what could have been a `AppointmentManagerAndBillingService` is deliberately split into `AppointmentScheduler` and `PaymentProcessor`.

---

### 2.6 Polymorphism

> *"Use polymorphism to handle alternatives based on type, rather than conditional (if/switch) logic."*

- Payment processing: instead of `if (method == "CARD") {...} else if (method == "INSURANCE") {...}`, the system calls `paymentStrategy.processPayment(amount)` on whatever concrete strategy is injected.
- Notifications: `channel.send(recipient, message)` works for email, SMS, and in-app channels.
- Pricing: `strategy.applyDiscount(price)` for insurance, percentage, or fixed discount — no branching.
- Appointment states: `state.confirm()`, `state.cancel()`, `state.complete()` — each state handles its own valid transitions.

---

### 2.7 Pure Fabrication

> *"Assign a highly cohesive set of responsibilities to an artificial class not found in the domain model, when required."*

- `NotificationDispatcher`: not a real-world entity, fabricated to encapsulate routing logic.
- `PricingCalculator`: not a domain concept; created to give the discount computation a clean home.
- `AppointmentScheduler`: orchestrator, not a domain object — its role is to coordinate the booking workflow.
- `NotificationFactory`: pure fabrication to centralize notification object creation.

---

### 2.8 Indirection

> *"Assign responsibility to an intermediate object to mediate between components to avoid direct coupling."*

- `NotificationDispatcher` sits between `AppointmentScheduler` (event source) and the concrete channels (email, SMS, in-app). Neither side knows about the other.
- `AppointmentRepository` (interface) sits between the business logic and the persistence layer.
- `PricingCalculator` intermediates between `PaymentProcessor` (which needs a final price) and the discount strategies.

---

## 3. Design Patterns

### 3.1 Strategy Pattern

**Intent:** Define a family of algorithms, encapsulate each one, and make them interchangeable.

**Applied to: Payment Methods and Pricing**

#### Payment Strategy

The system supports three payment methods: Credit Card, Insurance Coverage, and Digital Wallet. Rather than hard-coding payment logic with conditional branches, each method is encapsulated in its own class implementing `PaymentStrategy`.

```
<<interface>> PaymentStrategy
  + processPayment(amount: double): PaymentResult
  + getMethodName(): String

CreditCardPayment   implements PaymentStrategy
InsurancePayment    implements PaymentStrategy
DigitalWalletPayment implements PaymentStrategy
```

`PaymentProcessor` holds a reference to a `PaymentStrategy`. At runtime, the patient's choice determines which concrete strategy is injected. Adding a new payment method (e.g., cryptocurrency) requires only a new class — `PaymentProcessor` is never modified.

#### Pricing Strategy

Pricing adjustments (insurance discount, percentage discount, fixed reduction) are also strategies:

```
<<interface>> PricingStrategy
  + applyDiscount(basePrice: double): double
  + getDescription(): String

InsuranceDiscountStrategy    implements PricingStrategy
PercentageDiscountStrategy   implements PricingStrategy
FixedDiscountStrategy        implements PricingStrategy
```

`PricingCalculator` holds a `List<PricingStrategy>` and applies them sequentially. This enables stacking (e.g., insurance + promotional discount) without any change to the calculator.

**OCP benefit:** Adding `LoyaltyDiscount` in the future requires zero changes to existing classes.

```java
// PricingStrategy.java — the stable abstraction
public interface PricingStrategy {
    double applyDiscount(double basePrice);
    String getDescription();
}

// InsuranceDiscountStrategy.java — an extension, not a modification
public class InsuranceDiscountStrategy implements PricingStrategy {
    private final double coverageRate;
    public InsuranceDiscountStrategy(double coverageRate) { this.coverageRate = coverageRate; }

    @Override
    public double applyDiscount(double basePrice) {
        return basePrice * (1.0 - coverageRate);
    }
    @Override
    public String getDescription() {
        return String.format("Insurance coverage: %.0f%%", coverageRate * 100);
    }
}

// PricingCalculator.java — never changes when a new strategy is added
public double computeFinalPrice(double basePrice) {
    double price = basePrice;
    for (PricingStrategy strategy : strategies) {   // just iterates the injected list
        price = strategy.applyDiscount(price);
    }
    return Math.round(price * 100.0) / 100.0;
}
```

---

### 3.2 Observer Pattern

**Intent:** Define a one-to-many dependency so that when one object changes state, all its dependents are notified automatically.

**Applied to: Appointment State Changes → Notifications**

The `Appointment` is the **subject** (observable). `NotificationDispatcher` is the **observer**. When an appointment transitions to `CONFIRMED`, `CANCELLED`, or `COMPLETED`, it notifies all registered observers.

```
<<interface>> AppointmentObserver
  + onAppointmentEvent(event: AppointmentEvent): void

NotificationDispatcher  implements AppointmentObserver
AppointmentAuditLogger  implements AppointmentObserver  (extensibility example)

Appointment (Subject)
  - observers: List<AppointmentObserver>
  + subscribe(observer: AppointmentObserver): void
  + unsubscribe(observer: AppointmentObserver): void
  - notifyObservers(event: AppointmentEvent): void
  + confirm(): void       // triggers notification
  + cancel(): void        // triggers notification
  + complete(): void      // triggers notification
```

**Flow:**
1. Patient books appointment → `Appointment.confirm()` is called.
2. `Appointment` calls `notifyObservers(new AppointmentEvent(CONFIRMED, this))`.
3. `NotificationDispatcher.onAppointmentEvent(event)` receives the event.
4. Dispatcher builds a `Notification` via `NotificationFactory` and routes it to the appropriate channels.

**Code example:**

```java
// Appointment.java — Subject
public void confirm() {
    state.confirm(this);  // delegates to ScheduledState or ConfirmedState
}

// ScheduledState.java — calls notifyObservers after transition
public void confirm(Appointment appointment) {
    appointment.setState(new ConfirmedState());
    appointment.notifyObservers(new AppointmentEvent(EventType.CONFIRMED, appointment));
}

// Appointment.java — notifies all registered observers
public void notifyObservers(AppointmentEvent event) {
    for (AppointmentObserver observer : observers) {
        observer.onAppointmentEvent(event);
    }
}

// NotificationDispatcher.java — ConcreteObserver
@Override
public void onAppointmentEvent(AppointmentEvent event) {
    switch (event.getType()) {
        case CONFIRMED:
            dispatch(factory.createConfirmationNotification(event.getAppointment()));
            break;
        case CANCELLED:
            dispatch(factory.createCancellationNotification(event.getAppointment()));
            dispatch(factory.createDoctorCancellationNotification(event.getAppointment()));
            break;
        // ...
    }
}
```

**Extensibility:** Adding an `AppointmentAuditLogger` (for audit trails) only requires implementing `AppointmentObserver` and subscribing it — no change to `Appointment` or `NotificationDispatcher`.

---

### 3.3 State Pattern

**Intent:** Allow an object to alter its behavior when its internal state changes; the object will appear to change its class.

**Applied to: Appointment Lifecycle**

An appointment progresses through: `SCHEDULED → CONFIRMED → COMPLETED | CANCELLED`.  
Each state enforces which transitions are valid, preventing illegal operations (e.g., completing an already-cancelled appointment).

```
<<interface>> AppointmentState
  + confirm(appointment: Appointment): void
  + cancel(appointment: Appointment): void
  + complete(appointment: Appointment): void
  + getStateName(): String

ScheduledState    implements AppointmentState
ConfirmedState    implements AppointmentState
CompletedState    implements AppointmentState
CancelledState    implements AppointmentState
```

Each concrete state implements the valid transitions and throws `InvalidStateTransitionException` for invalid ones:

- `ScheduledState.confirm()` → sets state to `ConfirmedState` ✅
- `ScheduledState.complete()` → throws exception ❌
- `CompletedState.cancel()` → throws exception ❌
- `CancelledState.confirm()` → throws exception ❌

This eliminates complex `if/switch` chains in `Appointment` and makes each state's logic self-contained and independently testable.

```java
// AppointmentState.java — the interface each state implements
public interface AppointmentState {
    void confirm(Appointment appointment);
    void cancel(Appointment appointment);
    void complete(Appointment appointment);
    String getStateName();
}

// ScheduledState.java — only confirm() and cancel() are valid
public class ScheduledState implements AppointmentState {
    @Override
    public void confirm(Appointment appointment) {
        appointment.setState(new ConfirmedState());
        appointment.notifyObservers(new AppointmentEvent(EventType.CONFIRMED, appointment));
    }
    @Override
    public void cancel(Appointment appointment) {
        appointment.setState(new CancelledState());
        appointment.notifyObservers(new AppointmentEvent(EventType.CANCELLED, appointment));
    }
    @Override
    public void complete(Appointment appointment) {
        throw new InvalidStateTransitionException(getStateName(), "complete");  // ❌
    }
    @Override
    public String getStateName() { return "SCHEDULED"; }
}

// CompletedState.java — terminal state, nothing is valid
public class CompletedState implements AppointmentState {
    @Override
    public void cancel(Appointment appointment) {
        throw new InvalidStateTransitionException(getStateName(), "cancel");  // ❌
    }
    // ... all throw
}
```

---

### 3.4 Factory Pattern

**Intent:** Define an interface for creating objects, letting subclasses or factory methods decide which class to instantiate.

**Applied to: Notification Creation**

Notifications differ by type (confirmation, cancellation, reminder, schedule change), channel (email, SMS, in-app), and recipient. A `NotificationFactory` centralizes this construction logic.

```
NotificationFactory
  + createConfirmationNotification(appointment: Appointment): Notification
  + createCancellationNotification(appointment: Appointment): Notification
  + createReminderNotification(appointment: Appointment): Notification
  + createScheduleChangeNotification(appointment: Appointment, doctor: Doctor): Notification
```

Each factory method reads the involved user's `UserPreferences` to determine which channels should be used, constructs the `Notification` with the correct content template, and returns it ready for dispatch.

**Benefit:** `NotificationDispatcher` calls `factory.createConfirmationNotification(appointment)` without knowing anything about how notifications are constructed. Adding a new notification type (e.g., billing reminder) only requires a new factory method.

```java
// NotificationFactory.java — centralised creation
public Notification createConfirmationNotification(Appointment appointment) {
    Patient patient = appointment.getPatient();
    String subject = "Appointment Confirmed — " + appointment.getService().getName();
    String body = String.format(
            "Dear %s, your appointment with Dr. %s for %s has been confirmed on %s.",
            patient.getName(), appointment.getDoctor().getName(),
            appointment.getService().getName(), appointment.getSlot().getStart());
    return new Notification(patient, subject, body);
}

// Consumer (NotificationDispatcher) — zero knowledge of Notification construction
dispatch(factory.createConfirmationNotification(appointment));
```

---

### 3.5 Singleton Pattern

**Intent:** Ensure a class has only one instance and provide a global access point to it.

**Applied to: SystemConfigurationManager**

The `SystemConfigurationManager` holds system-wide settings: default appointment duration, notification retry policies, supported payment methods, and feature flags. There must be exactly one instance throughout the application's lifecycle.

```
SystemConfigurationManager
  - instance: SystemConfigurationManager  [static]
  - config: Map<String, Object>
  - SystemConfigurationManager()          [private constructor]
  + getInstance(): SystemConfigurationManager  [static]
  + get(key: String): Object
  + set(key: String, value: Object): void
  + loadFromFile(path: String): void
```

**Thread-safety note:** The implementation uses double-checked locking (or initialization-on-demand holder idiom in Java) to ensure thread-safe lazy instantiation.

```java
// SystemConfigurationManager.java — Singleton via holder idiom (thread-safe, no synchronization)
public class SystemConfigurationManager {

    private static final class Holder {
        private static final SystemConfigurationManager INSTANCE =
                new SystemConfigurationManager();
    }

    private SystemConfigurationManager() {
        config.put("default.appointment.duration.minutes", 30);
        config.put("notification.retry.attempts", 3);
        // ...
    }

    public static SystemConfigurationManager getInstance() {
        return Holder.INSTANCE;  // guaranteed single instance
    }
}

// Usage — anywhere in the codebase, always the same object
SystemConfigurationManager cfg = SystemConfigurationManager.getInstance();
int duration = cfg.getInt("default.appointment.duration.minutes", 30);
```

---

## Summary Table

| Principle / Pattern | Where Applied | Key Benefit |
|---|---|---|
| **SRP** | All service classes | Each class has one reason to change |
| **OCP** | PaymentStrategy, PricingStrategy, NotificationChannel | Extend without modifying |
| **LSP** | User hierarchy, all strategy implementations | Safe substitution |
| **ISP** | Schedulable, AvailabilityManageable, SystemAdministrable | Role-specific interfaces |
| **DIP** | All service constructors | Testability via injection |
| **Creator** | AppointmentScheduler, NotificationFactory, PaymentProcessor | Responsibility follows data ownership |
| **Information Expert** | AvailabilityService, PricingCalculator, Appointment | Responsibility follows information |
| **Controller** | AppointmentController, UserController, etc. | Clean separation of entry points |
| **Low Coupling** | Interface-based dependencies | Independent evolution of components |
| **High Cohesion** | All domain & service classes | Focused, understandable classes |
| **Polymorphism** | Payment, Pricing, Notification, State | No conditional branching |
| **Pure Fabrication** | NotificationDispatcher, PricingCalculator | Cohesive artificial helpers |
| **Indirection** | NotificationDispatcher, Repository interfaces | Decoupled communication |
| **Strategy** | PaymentStrategy, PricingStrategy | Interchangeable algorithms |
| **Observer** | Appointment → NotificationDispatcher | Automatic event-driven notification |
| **State** | AppointmentState hierarchy | Clean lifecycle management |
| **Factory** | NotificationFactory | Centralized object construction |
| **Singleton** | SystemConfigurationManager | Single system configuration source |
