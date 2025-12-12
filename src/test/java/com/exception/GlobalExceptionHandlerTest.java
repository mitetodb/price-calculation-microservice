package com.exception;

import com.controller.CustomerBaseFeeController;
import com.model.dto.CustomerBaseFeeDTO;
import com.service.CustomerBaseFeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Note: This test verifies that GlobalExceptionHandler is configured.
 * To fully test validation handling, you would need to add @Valid annotations
 * and @NotNull/@NotEmpty validations to your DTOs, then test with invalid input.
 * The GlobalExceptionHandler handles MethodArgumentNotValidException from validation.
 */
@WebMvcTest({CustomerBaseFeeController.class, GlobalExceptionHandler.class})
@ActiveProfiles("test")
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerBaseFeeService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void controllerWorksWithHandlerConfigured() throws Exception {
        UUID customerId = UUID.randomUUID();
        CustomerBaseFeeDTO dto = new CustomerBaseFeeDTO();
        dto.setBaseServiceFee(100.0);

        doNothing().when(service).createBaseFee(any(UUID.class), any(CustomerBaseFeeDTO.class));

        // This verifies the controller and exception handler work together
        mockMvc.perform(post("/customers/{customerId}/base-fee", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }
}
