package com.controller;

import com.model.dto.CustomerBaseFeeDTO;
import com.service.CustomerBaseFeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/customers")
public class CustomerBaseFeeController {

    private final CustomerBaseFeeService service;

    public CustomerBaseFeeController(CustomerBaseFeeService service) {
        this.service = service;
    }

    @PostMapping("/{customerId}/base-fee")
    public ResponseEntity<Void> createBaseFee(@PathVariable("customerId") UUID customerId,
                                              @RequestBody CustomerBaseFeeDTO dto) {
        service.createBaseFee(customerId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{customerId}/base-fee")
    public ResponseEntity<Void> updateBaseFee(@PathVariable("customerId") UUID customerId,
                                              @RequestBody CustomerBaseFeeDTO dto) {
        service.updateBaseFee(customerId, dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{customerId}/base-fee")
    public ResponseEntity<Void> deleteBaseFee(@PathVariable("customerId") UUID customerId) {
        service.deleteBaseFee(customerId);
        return ResponseEntity.noContent().build();
    }
}
