package com.service;

import com.model.PriceCalculationRequest;
import com.model.PriceCalculationResponse;
import org.springframework.stereotype.Service;

@Service
public class PriceCalculationService {

    private final ContractPricingService contractPricing;

    public PriceCalculationService(ContractPricingService contractPricing) {
        this.contractPricing = contractPricing;
    }

    public PriceCalculationResponse calculate(PriceCalculationRequest request) {

        double productTotal = request.getProductTotal();

        // base service fee price on contract
        double baseRate = contractPricing.getBaseRate(
                request.getCustomerId(),
                request.getCountry(),
                request.getCategory()
        );

        // category multiplier +25%
        double categoryMultiplier = switch (request.getCategory()) {
            case "S" -> 1.00;
            case "M" -> 1.25; // +25%
            case "L" -> 1.50; // +50%
            case "XL" -> 1.75; // +75%
            default -> 1.0;
        };

        // type multiplier +50%
        double typeMultiplier = switch (request.getType()) {
            case "FORWARDING_TO_CLIENT" -> 1.00;
            case "RETURN_BACK_TO_SELLER" -> 1.50; // +50%
            default -> 1.0;
        };

        double testPurchaseFee = baseRate * categoryMultiplier * typeMultiplier;

        // postage prices
        double postage = switch (request.getCategory()) {
            case "S" -> 10.00;
            case "M" -> 15.00;
            case "L" -> 20.00;
            case "XL" -> 30.00;
            default -> 10.0;
        };

        // response
        PriceCalculationResponse response = new PriceCalculationResponse();
        response.setTestPurchaseFee(round(testPurchaseFee));
        response.setPostageFee(round(postage));
        response.setProductPrice(round(productTotal));

        return response;
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

}
