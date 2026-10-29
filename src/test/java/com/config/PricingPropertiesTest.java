package com.config;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PricingPropertiesTest {

    @Test
    void lookupIsCaseInsensitiveAndFallsBackToDefault() {
        PricingProperties p = new PricingProperties();
        p.setDefaultBaseRate(150.0);
        p.setCountryBaseRates(Map.of("GERMANY", 150.0, "AUSTRIA", 160.0, "Bulgaria", 120.0));

        assertThat(p.baseRateFor("austria")).isEqualTo(160.0);
        assertThat(p.baseRateFor(" AUSTRIA ")).isEqualTo(160.0);
        assertThat(p.baseRateFor("BULGARIA")).isEqualTo(120.0);
        assertThat(p.baseRateFor("FRANCE")).isEqualTo(150.0);
        assertThat(p.baseRateFor(null)).isEqualTo(150.0);
    }
}
