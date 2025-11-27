package com.service;

import com.model.PriceCalculationRequest;
import com.model.PriceCalculationResponse;
import org.springframework.stereotype.Service;

@Service
public class PriceCalculationService {

    public PriceCalculationResponse calculate(PriceCalculationRequest req) {

        double msFee = switch (req.getCategory()) {
            case "S" -> 25.00;
            case "M" -> 30.00;
            case "L" -> 35.00;
            case "XL" -> 40.00;
            default -> 30.00;
        };

        double clientFee = Math.round(msFee * 0.75);

        double total = req.getProductBasePrice()
                + req.getPostage()
                + msFee
                + clientFee;

        return new PriceCalculationResponse(
                msFee,
                clientFee,
                total,
                resolveCurrency(req.getCountry())
        );
    }

    private String resolveCurrency(String country) {
        return switch (country.toLowerCase()) {
            case "germany", "france", "italy", "spain", "austria" -> "EUR";
            case "bulgaria" -> "BGN";
            case "poland" -> "PLN";
            case "england", "uk" -> "GBP";
            default -> "EUR";
        };
    }
}
