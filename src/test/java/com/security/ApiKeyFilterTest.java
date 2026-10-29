package com.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "pricing.security.api-key=test-key-123")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiKeyFilterTest {

    private static final String BODY = "{\"country\":\"GERMANY\",\"category\":\"S\",\"type\":\"FORWARDING_TO_CLIENT\",\"productTotal\":10}";

    @Autowired private MockMvc mvc;

    @Test
    void rejectsMissingKey() throws Exception {
        mvc.perform(post("/pricing/calculate").contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rejectsWrongKey() throws Exception {
        mvc.perform(post("/pricing/calculate").header("X-API-Key", "wrong")
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void acceptsCorrectKey() throws Exception {
        mvc.perform(post("/pricing/calculate").header("X-API-Key", "test-key-123")
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isOk());
    }

    @Test
    void healthStaysOpen() throws Exception {
        mvc.perform(get("/actuator/health")).andExpect(status().isOk());
    }

    @Test
    void openApiDocsAvailable() throws Exception {
        mvc.perform(get("/v3/api-docs")).andExpect(status().isOk());
    }

    @Test
    void failsFastWhenKeyRequiredButMissing() {
        assertThatThrownBy(() -> new ApiKeyFilter("", "X-API-Key", true))
                .isInstanceOf(IllegalStateException.class);
    }
}
