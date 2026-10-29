package com.service;

import com.config.PricingProperties;
import com.model.CustomerBaseFee;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ContractPricingService {

    private final CustomerBaseFeeService customerBaseFeeService;
    private final PricingProperties pricingProperties;

    public ContractPricingService(CustomerBaseFeeService customerBaseFeeService,
                                  PricingProperties pricingProperties) {
        this.customerBaseFeeService = customerBaseFeeService;
        this.pricingProperties = pricingProperties;
    }

    public double getBaseRate(UUID customerId, String country, String category) {
        // 1) contract-specific fee from DB, 2) configured country rate, 3) configured default
        return customerBaseFeeService.findByCustomerId(customerId)
                .map(CustomerBaseFee::getBaseServiceFee)
                .orElseGet(() -> pricingProperties.baseRateFor(country));
    }
}
