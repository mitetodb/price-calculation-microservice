package com.controller;

import com.model.PriceCalculationRequest;
import com.model.PriceCalculationResponse;
import com.service.PriceCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pricing")
@RequiredArgsConstructor
public class PriceCalculationController {

    private final PriceCalculationService service;

    @Value("${pricing.demo-mode:false}")
    private boolean demoMode;

    @PostMapping("/calculate")
    public ResponseEntity<PriceCalculationResponse> calculate(@RequestBody PriceCalculationRequest request) {

        if (demoMode) {
            return ResponseEntity.ok(generateDemoPrice(request));
        }

        // response
        PriceCalculationResponse response = service.calculate(request);

        return ResponseEntity.ok(response);
    }

    private PriceCalculationResponse generateDemoPrice(PriceCalculationRequest request) {
        return service.calculate(request);
    }
}
