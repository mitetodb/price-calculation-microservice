package com.controller;

import com.model.PriceCalculationRequest;
import com.model.PriceCalculationResponse;
import com.service.PriceCalculationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/price")
@RequiredArgsConstructor
@CrossOrigin("*")
public class PriceCalculationController {

    private final PriceCalculationService priceCalculationService;

    @PostMapping("/calculate")
    public ResponseEntity<PriceCalculationResponse> calculate(
            @Valid @RequestBody PriceCalculationRequest request) {

        return ResponseEntity.ok(
                priceCalculationService.calculate(request)
        );
    }
}
