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

    @Test
    void calculate_ShouldCalculateCorrectly_ForCategoryS() {
        UUID customerId = UUID.randomUUID();
        PriceCalculationRequest request = new PriceCalculationRequest();
        request.setCustomerId(customerId);
        request.setCountry("GERMANY");
        request.setCategory("S");  // x1.00
        request.setType("FORWARDING_TO_CLIENT"); // x1.00
        request.setProductTotal(50.0);

        PriceCalculationResponse response = priceCalculationService.calculate(request);

        // 150.00 * 1.00 * 1.00 = 150.00
        assertThat(response.getTestPurchaseFee()).isEqualTo(150.00);
        assertThat(response.getPostageFee()).isEqualTo(10.00);
        assertThat(response.getProductPrice()).isEqualTo(50.0);
    }

    @Test
    void calculate_ShouldCalculateCorrectly_ForCategoryM() {
        UUID customerId = UUID.randomUUID();
        PriceCalculationRequest request = new PriceCalculationRequest();
        request.setCustomerId(customerId);
        request.setCountry("GERMANY");
        request.setCategory("M");  // x1.25
        request.setType("FORWARDING_TO_CLIENT"); // x1.00
        request.setProductTotal(75.0);

        PriceCalculationResponse response = priceCalculationService.calculate(request);

        // 150.00 * 1.25 * 1.00 = 187.50
        assertThat(response.getTestPurchaseFee()).isEqualTo(187.50);
        assertThat(response.getPostageFee()).isEqualTo(15.00);
    }

    @Test
    void calculate_ShouldCalculateCorrectly_ForCategoryL() {
        UUID customerId = UUID.randomUUID();
        PriceCalculationRequest request = new PriceCalculationRequest();
        request.setCustomerId(customerId);
        request.setCountry("GERMANY");
        request.setCategory("L");  // x1.50
        request.setType("FORWARDING_TO_CLIENT"); // x1.00
        request.setProductTotal(100.0);

        PriceCalculationResponse response = priceCalculationService.calculate(request);

        // 150.00 * 1.50 * 1.00 = 225.00
        assertThat(response.getTestPurchaseFee()).isEqualTo(225.00);
        assertThat(response.getPostageFee()).isEqualTo(20.00);
    }

    @Test
    void calculate_ShouldCalculateCorrectly_ForCategoryXL() {
        UUID customerId = UUID.randomUUID();
        PriceCalculationRequest request = new PriceCalculationRequest();
        request.setCustomerId(customerId);
        request.setCountry("GERMANY");
        request.setCategory("XL");  // x1.75
        request.setType("FORWARDING_TO_CLIENT"); // x1.00
        request.setProductTotal(200.0);

        PriceCalculationResponse response = priceCalculationService.calculate(request);

        // 150.00 * 1.75 * 1.00 = 262.50
        assertThat(response.getTestPurchaseFee()).isEqualTo(262.50);
        assertThat(response.getPostageFee()).isEqualTo(30.00);
    }

    @Test
    void calculate_ShouldCalculateCorrectly_ForReturnBackToSeller() {
        UUID customerId = UUID.randomUUID();
        PriceCalculationRequest request = new PriceCalculationRequest();
        request.setCustomerId(customerId);
        request.setCountry("GERMANY");
        request.setCategory("S");  // x1.00
        request.setType("RETURN_BACK_TO_SELLER"); // x1.50
        request.setProductTotal(50.0);

        PriceCalculationResponse response = priceCalculationService.calculate(request);

        // 150.00 * 1.00 * 1.50 = 225.00
        assertThat(response.getTestPurchaseFee()).isEqualTo(225.00);
    }

    @Test
    void calculate_ShouldCalculateCorrectly_ForAustria() {
        UUID customerId = UUID.randomUUID();
        PriceCalculationRequest request = new PriceCalculationRequest();
        request.setCustomerId(customerId);
        request.setCountry("AUSTRIA");
        request.setCategory("S");  // x1.00
        request.setType("FORWARDING_TO_CLIENT"); // x1.00
        request.setProductTotal(50.0);

        PriceCalculationResponse response = priceCalculationService.calculate(request);

        // 160.00 * 1.00 * 1.00 = 160.00
        assertThat(response.getTestPurchaseFee()).isEqualTo(160.00);
    }

    @Test
    void calculate_ShouldUseDefaultMultipliers_ForUnknownCategory() {
        UUID customerId = UUID.randomUUID();
        PriceCalculationRequest request = new PriceCalculationRequest();
        request.setCustomerId(customerId);
        request.setCountry("GERMANY");
        request.setCategory("UNKNOWN");  // default x1.0
        request.setType("FORWARDING_TO_CLIENT"); // x1.00
        request.setProductTotal(50.0);

        PriceCalculationResponse response = priceCalculationService.calculate(request);

        // 150.00 * 1.0 * 1.00 = 150.00
        assertThat(response.getTestPurchaseFee()).isEqualTo(150.00);
        assertThat(response.getPostageFee()).isEqualTo(10.00); // default for unknown category
    }

    @Test
    void calculate_ShouldUseDefaultMultipliers_ForUnknownType() {
        UUID customerId = UUID.randomUUID();
        PriceCalculationRequest request = new PriceCalculationRequest();
        request.setCustomerId(customerId);
        request.setCountry("GERMANY");
        request.setCategory("S");  // x1.00
        request.setType("UNKNOWN_TYPE"); // default x1.0
        request.setProductTotal(50.0);

        PriceCalculationResponse response = priceCalculationService.calculate(request);

        // 150.00 * 1.00 * 1.0 = 150.00
        assertThat(response.getTestPurchaseFee()).isEqualTo(150.00);
    }

    @Test
    void calculate_ShouldRoundCorrectly_ForDecimalValues() {
        UUID customerId = UUID.randomUUID();
        CustomerBaseFee customFee = new CustomerBaseFee(customerId, 100.333, LocalDateTime.now());
        customerBaseFeeRepository.save(customFee);

        PriceCalculationRequest request = new PriceCalculationRequest();
        request.setCustomerId(customerId);
        request.setCountry("GERMANY");
        request.setCategory("M");  // x1.25
        request.setType("FORWARDING_TO_CLIENT");  // x1.0
        request.setProductTotal(33.333);

        PriceCalculationResponse response = priceCalculationService.calculate(request);

        // 100.333 * 1.25 * 1.0 = 125.41625 -> rounded to 125.42
        assertThat(response.getTestPurchaseFee()).isEqualTo(125.42);
        assertThat(response.getProductPrice()).isEqualTo(33.33);
    }

    @Test
    void calculate_ShouldHandleComplexCalculation() {
        UUID customerId = UUID.randomUUID();
        CustomerBaseFee customFee = new CustomerBaseFee(customerId, 200.00, LocalDateTime.now());
        customerBaseFeeRepository.save(customFee);

        PriceCalculationRequest request = new PriceCalculationRequest();
        request.setCustomerId(customerId);
        request.setCountry("GERMANY");
        request.setCategory("XL");  // x1.75
        request.setType("RETURN_BACK_TO_SELLER"); // x1.50
        request.setProductTotal(500.0);

        PriceCalculationResponse response = priceCalculationService.calculate(request);

        // 200.00 * 1.75 * 1.50 = 525.00
        assertThat(response.getTestPurchaseFee()).isEqualTo(525.00);
        assertThat(response.getPostageFee()).isEqualTo(30.00);
        assertThat(response.getProductPrice()).isEqualTo(500.0);
    }
}
