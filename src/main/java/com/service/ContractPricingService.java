package com.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ContractPricingService {

    public double getBaseRate(UUID customerId, String country, String category) {
        // todo
        return switch (country.toUpperCase()) {
            case "GERMANY" -> 150.00;  // € 150
            case "AUSTRIA" -> 160.00;  // € 160
            default -> 150.00;    // € 150 default
        };
    }

}
