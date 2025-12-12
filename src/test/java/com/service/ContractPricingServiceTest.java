package com.service;

import com.model.CustomerBaseFee;
import com.repository.CustomerBaseFeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ContractPricingServiceTest {

    @Autowired
    private ContractPricingService contractPricingService;

    @Autowired
    private CustomerBaseFeeRepository customerBaseFeeRepository;

    @BeforeEach
    void setUp() {
        customerBaseFeeRepository.deleteAll();
    }

    @Test
    void getBaseRate_ShouldReturnCustomFee_WhenCustomerHasCustomFee() {
        UUID customerId = UUID.randomUUID();
        CustomerBaseFee customFee = new CustomerBaseFee(customerId, 200.00, LocalDateTime.now());
        customerBaseFeeRepository.save(customFee);

        double baseRate = contractPricingService.getBaseRate(customerId, "GERMANY", "S");

        assertThat(baseRate).isEqualTo(200.00);
    }

    @Test
    void getBaseRate_ShouldReturnDefaultForGermany_WhenNoCustomFee() {
        UUID customerId = UUID.randomUUID();

        double baseRate = contractPricingService.getBaseRate(customerId, "GERMANY", "S");

        assertThat(baseRate).isEqualTo(150.00);
    }

    @Test
    void getBaseRate_ShouldReturnDefaultForAustria_WhenNoCustomFee() {
        UUID customerId = UUID.randomUUID();

        double baseRate = contractPricingService.getBaseRate(customerId, "AUSTRIA", "M");

        assertThat(baseRate).isEqualTo(160.00);
    }

    @Test
    void getBaseRate_ShouldReturnDefaultForUnknownCountry_WhenNoCustomFee() {
        UUID customerId = UUID.randomUUID();

        double baseRate = contractPricingService.getBaseRate(customerId, "UNKNOWN_COUNTRY", "L");

        assertThat(baseRate).isEqualTo(150.00);
    }

    @Test
    void getBaseRate_ShouldReturnDefaultForLowercaseCountry_WhenNoCustomFee() {
        UUID customerId = UUID.randomUUID();

        double baseRate = contractPricingService.getBaseRate(customerId, "germany", "S");

        assertThat(baseRate).isEqualTo(150.00);
    }

    @Test
    void getBaseRate_ShouldReturnCustomFee_WhenCustomerHasCustomFeeAndDifferentCountry() {
        UUID customerId = UUID.randomUUID();
        CustomerBaseFee customFee = new CustomerBaseFee(customerId, 175.00, LocalDateTime.now());
        customerBaseFeeRepository.save(customFee);

        // Custom fee should take precedence regardless of country
        double baseRate = contractPricingService.getBaseRate(customerId, "AUSTRIA", "XL");

        assertThat(baseRate).isEqualTo(175.00);
    }

    @Test
    void getBaseRate_ShouldReturnDefaultForDifferentCustomer_WhenOtherCustomerHasCustomFee() {
        UUID customerWithFee = UUID.randomUUID();
        UUID customerWithoutFee = UUID.randomUUID();
        CustomerBaseFee customFee = new CustomerBaseFee(customerWithFee, 300.00, LocalDateTime.now());
        customerBaseFeeRepository.save(customFee);

        double baseRate = contractPricingService.getBaseRate(customerWithoutFee, "GERMANY", "S");

        assertThat(baseRate).isEqualTo(150.00);
    }
}

