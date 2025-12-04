package com.service;

import com.model.CustomerBaseFee;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ContractPricingService {

    private final CustomerBaseFeeService customerBaseFeeService;

    public ContractPricingService(CustomerBaseFeeService customerBaseFeeService) {
        this.customerBaseFeeService = customerBaseFeeService;
    }

    public double getBaseRate(UUID customerId, String country, String category) {
        // 1) Try to load baseServiceFee from DB for this customer
        return customerBaseFeeService.findByCustomerId(customerId)
                .map(CustomerBaseFee::getBaseServiceFee)
                .orElseGet(() -> getDefaultBaseRateByCountry(country));
    }

    private double getDefaultBaseRateByCountry(String country) {
        // todo
        return switch (country.toUpperCase()) {
            case "GERMANY" -> 150.00;  // € 150
            case "AUSTRIA" -> 160.00;  // € 160
            default -> 150.00;    // € 150 default
        };
    }

}
