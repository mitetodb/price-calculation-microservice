package com.controller;

import com.model.PriceCalculationRequest;
import com.model.PriceCalculationResponse;
import com.service.PriceCalculationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/price")
public class PriceCalculationController {

    private final PriceCalculationService service;

    public PriceCalculationController(PriceCalculationService service) {
        this.service = service;
    }

    @PostMapping("/calculate")
    public PriceCalculationResponse calculate(@RequestBody PriceCalculationRequest request) {
        return service.calculate(request);
    }
}
