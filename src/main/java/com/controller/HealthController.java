package com.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/health/details")
    public Map<String, Object> healthDetails() {
        return Map.of(
                "status", "UP",
                "engineRules", "country multipliers + category fees + FX currency table loaded",
                "timestamp", System.currentTimeMillis()
        );
    }
}
