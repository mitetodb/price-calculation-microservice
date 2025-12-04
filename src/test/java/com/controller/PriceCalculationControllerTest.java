package com.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.model.PriceCalculationRequest;
import com.model.PriceCalculationResponse;
import com.service.PriceCalculationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PriceCalculationController.class)
@ActiveProfiles("test")
class PriceCalculationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PriceCalculationService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void calculate_ReturnsOk() throws Exception {
        PriceCalculationResponse mockResponse = new PriceCalculationResponse();
        mockResponse.setTestPurchaseFee(150.0);
        mockResponse.setPostageFee(10.0);
        mockResponse.setProductPrice(99.99);

        when(service.calculate(any(PriceCalculationRequest.class))).thenReturn(mockResponse);

        PriceCalculationRequest request = new PriceCalculationRequest();
        request.setCustomerId(UUID.randomUUID());
        request.setCountry("GERMANY");
        request.setCategory("S");
        request.setType("FORWARDING_TO_CLIENT");
        request.setProductTotal(99.99);

        mockMvc.perform(post("/pricing/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.testPurchaseFee").value(150.0))
                .andExpect(jsonPath("$.postageFee").value(10.0));
    }
}
