package com.healthcare.appointment.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton holding system-wide configuration parameters.
 *
 * DESIGN PATTERN - Singleton
 * Thread-safe via initialization-on-demand holder idiom.
 *
 * SOLID - SRP: Only holds and provides system configuration.
 * GRASP - Information Expert: The single authoritative source of system config.
 */
public class SystemConfigurationManager {

    private final Map<String, Object> config = new HashMap<>();

    // Initialization-on-demand holder - thread-safe without synchronization
    // overhead
    private static final class Holder {
        private static final SystemConfigurationManager INSTANCE = new SystemConfigurationManager();
    }

    private SystemConfigurationManager() {
        // Default configuration values
        config.put("default.appointment.duration.minutes", 30);
        config.put("notification.retry.attempts", 3);
        config.put("supported.payment.methods", "CREDIT_CARD,INSURANCE,DIGITAL_WALLET");
        config.put("system.name", "HealthCare Appointment Platform");
        config.put("reminder.hours.before", 24);
    }

    public static SystemConfigurationManager getInstance() {
        return Holder.INSTANCE;
    }

    public Object get(String key) {
        return config.get(key);
    }

    public void set(String key, Object value) {
        config.put(key, value);
    }

    public String getString(String key) {
        Object value = config.get(key);
        return value != null ? value.toString() : null;
    }

    public int getInt(String key, int defaultValue) {
        Object value = config.get(key);
        if (value instanceof Integer)
            return (Integer) value;
        return defaultValue;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("SystemConfigurationManager {\n");
        config.forEach((k, v) -> sb.append("  ").append(k).append(" = ").append(v).append("\n"));
        sb.append("}");
        return sb.toString();
    }
}
