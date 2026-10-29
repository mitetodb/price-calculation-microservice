package com.service;

import com.model.PriceCalculationRequest;
import com.model.PriceCalculationResponse;
import com.repository.CustomerBaseFeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Regression test: the config-driven country rates must produce exactly the
 * same results as the previous hardcoded switch.
 */
@SpringBootTest
@ActiveProfiles("test")
class PricingBehaviourUnchangedTest {

    @Autowired private PriceCalculationService service;
    @Autowired private CustomerBaseFeeRepository repository;

    @BeforeEach
    void clean() { repository.deleteAll(); }

    /** Copy of the original hardcoded logic (before this change). */
    private static double legacyBaseRate(String country) {
        return switch (country.toUpperCase()) {
            case "GERMANY" -> 150.00;
            case "AUSTRIA" -> 160.00;
            default -> 150.00;
        };
    }

    private static double legacyFee(String country, String category, String type) {
        double cat = switch (category) { case "S" -> 1.00; case "M" -> 1.25; case "L" -> 1.50; case "XL" -> 1.75; default -> 1.0; };
        double typ = switch (type) { case "FORWARDING_TO_CLIENT" -> 1.00; case "RETURN_BACK_TO_SELLER" -> 1.50; default -> 1.0; };
        return Math.round(legacyBaseRate(country) * cat * typ * 100.0) / 100.0;
    }

    static Stream<Arguments> matrix() {
        List<Arguments> args = new ArrayList<>();
        for (String c : List.of("GERMANY", "germany", "Austria", "AUSTRIA", "FRANCE", "UNKNOWN"))
            for (String cat : List.of("S", "M", "L", "XL", "XXL"))
                for (String t : List.of("FORWARDING_TO_CLIENT", "RETURN_BACK_TO_SELLER", "OTHER"))
                    args.add(Arguments.of(c, cat, t));
        return args.stream();
    }

    @ParameterizedTest
    @MethodSource("matrix")
    void feeMatchesLegacyBehaviour(String country, String category, String type) {
        PriceCalculationRequest r = new PriceCalculationRequest();
        r.setCustomerId(UUID.randomUUID());
        r.setCountry(country);
        r.setCategory(category);
        r.setType(type);
        r.setProductTotal(49.99);

        PriceCalculationResponse resp = service.calculate(r);

        assertThat(resp.getTestPurchaseFee()).isEqualTo(legacyFee(country, category, type));
        assertThat(resp.getProductPrice()).isEqualTo(49.99);
    }
}
