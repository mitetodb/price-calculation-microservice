package com.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PriceCalculationRequest {

    @NotNull
    @Min(0)
    private Double productBasePrice;

    @NotBlank
    private String country;

    @NotBlank
    private String category;

    @NotNull
    @Min(0)
    private Double postage;
}