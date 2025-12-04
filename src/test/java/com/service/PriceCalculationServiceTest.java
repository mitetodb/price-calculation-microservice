package com.service;

import com.model.CustomerBaseFee;
import com.model.PriceCalculationRequest;
import com.model.PriceCalculationResponse;
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
class PriceCalculationServiceTest {

    @Autowired
    private PriceCalculationService priceCalculationService;

    @Autowired
    private CustomerBaseFeeRepository customerBaseFeeRepository;

    @BeforeEach
    void setUp() {
        customerBaseFeeRepository.deleteAll();
    }

    @Test
    void calculate_ShouldUseDefaultCountryRate_WhenNoCustomFee() {
        UUID customerId = UUID.randomUUID();

        PriceCalculationRequest request = new PriceCalculationRequest();
        request.setCustomerId(customerId);
        request.setCountry("GERMANY"); // Default 150.00
        request.setCategory("S");      // x1.0
        request.setType("FORWARDING_TO_CLIENT"); // x1.0
        request.setProductTotal(50.0);

        PriceCalculationResponse response = priceCalculationService.calculate(request);

        assertThat(response.getTestPurchaseFee()).isEqualTo(150.00);
        assertThat(response.getPostageFee()).isEqualTo(10.00);
    }

    @Test
    void calculate_ShouldUseCustomFee_WhenFeeExistsInDb() {
        UUID customerId = UUID.randomUUID();
        CustomerBaseFee customFee = new CustomerBaseFee(customerId, 100.00, LocalDateTime.now());
        customerBaseFeeRepository.save(customFee);

        PriceCalculationRequest request = new PriceCalculationRequest();
        request.setCustomerId(customerId);
        request.setCountry("GERMANY"); 
        request.setCategory("M");      // x1.25
        request.setType("RETURN_BACK_TO_SELLER"); // x1.50
        request.setProductTotal(10.0);

        PriceCalculationResponse response = priceCalculationService.calculate(request);

        // Calculation: 100.00 * 1.25 * 1.50 = 187.50
        assertThat(response.getTestPurchaseFee()).isEqualTo(187.50);
        assertThat(response.getPostageFee()).isEqualTo(15.00);
    }
}
