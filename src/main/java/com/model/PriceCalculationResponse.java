package com.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PriceCalculationResponse {

    private Double msFee;
    private Double clientServiceFee;
    private Double totalCost;
    private String currency;
}
