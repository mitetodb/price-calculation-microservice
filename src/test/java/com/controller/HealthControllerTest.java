package com.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HealthController.class)
@ActiveProfiles("test")
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void healthDetails_ReturnsOk() throws Exception {
        mockMvc.perform(get("/health/details"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.engineRules").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void healthDetails_ContainsCorrectStatus() throws Exception {
        mockMvc.perform(get("/health/details"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void healthDetails_ContainsTimestamp() throws Exception {
        mockMvc.perform(get("/health/details"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.timestamp").isNumber());
    }

    @Test
    void healthDetails_ContainsEngineRules() throws Exception {
        mockMvc.perform(get("/health/details"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.engineRules").value("country multipliers + category fees + FX currency table loaded"));
    }
}

