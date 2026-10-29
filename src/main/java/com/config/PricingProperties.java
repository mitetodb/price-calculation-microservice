package com.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Country base rates used when a customer has no contract-specific base fee.
 * Defaults (application.properties) keep the original behaviour:
 * GERMANY=150, AUSTRIA=160, everything else=150.
 */
@ConfigurationProperties(prefix = "pricing")
public class PricingProperties {

    private double defaultBaseRate = 150.00;
    private Map<String, Double> countryBaseRates = new HashMap<>();

    public double getDefaultBaseRate() { return defaultBaseRate; }
    public void setDefaultBaseRate(double defaultBaseRate) { this.defaultBaseRate = defaultBaseRate; }

    public Map<String, Double> getCountryBaseRates() { return countryBaseRates; }
    public void setCountryBaseRates(Map<String, Double> countryBaseRates) { this.countryBaseRates = countryBaseRates; }

    public double baseRateFor(String country) {
        if (country == null || country.isBlank()) {
            return defaultBaseRate;
        }
        String key = country.trim().toUpperCase(Locale.ROOT);
        for (Map.Entry<String, Double> e : countryBaseRates.entrySet()) {
            if (e.getKey().toUpperCase(Locale.ROOT).equals(key) && e.getValue() != null) {
                return e.getValue();
            }
        }
        return defaultBaseRate;
    }
}
