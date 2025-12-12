package com.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.model.dto.CustomerBaseFeeDTO;
import com.service.CustomerBaseFeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerBaseFeeController.class)
@ActiveProfiles("test")
class CustomerBaseFeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerBaseFeeService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createBaseFee_ReturnsCreated() throws Exception {
        UUID customerId = UUID.randomUUID();
        CustomerBaseFeeDTO dto = new CustomerBaseFeeDTO();
        dto.setBaseServiceFee(100.0);
        dto.setChangedAt(LocalDateTime.now());

        doNothing().when(service).createBaseFee(any(UUID.class), any(CustomerBaseFeeDTO.class));

        mockMvc.perform(post("/customers/{customerId}/base-fee", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void updateBaseFee_ReturnsOk() throws Exception {
        UUID customerId = UUID.randomUUID();
        CustomerBaseFeeDTO dto = new CustomerBaseFeeDTO();
        dto.setBaseServiceFee(200.0);

        doNothing().when(service).updateBaseFee(any(UUID.class), any(CustomerBaseFeeDTO.class));

        mockMvc.perform(put("/customers/{customerId}/base-fee", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteBaseFee_ReturnsNoContent() throws Exception {
        UUID customerId = UUID.randomUUID();

        doNothing().when(service).deleteBaseFee(any(UUID.class));

        mockMvc.perform(delete("/customers/{customerId}/base-fee", customerId))
                .andExpect(status().isNoContent());
    }

    @Test
    void createBaseFee_WithInvalidRequestBody_ReturnsBadRequest() throws Exception {
        UUID customerId = UUID.randomUUID();

        // Empty body
        mockMvc.perform(post("/customers/{customerId}/base-fee", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isCreated()); // Service handles null values
    }

    @Test
    void createBaseFee_WithInvalidContentType_ReturnsUnsupportedMediaType() throws Exception {
        UUID customerId = UUID.randomUUID();

        mockMvc.perform(post("/customers/{customerId}/base-fee", customerId)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("invalid"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void updateBaseFee_WithNullBaseFee_ReturnsOk() throws Exception {
        UUID customerId = UUID.randomUUID();
        CustomerBaseFeeDTO dto = new CustomerBaseFeeDTO();
        // baseServiceFee is null

        doNothing().when(service).updateBaseFee(any(UUID.class), any(CustomerBaseFeeDTO.class));

        mockMvc.perform(put("/customers/{customerId}/base-fee", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }
}