package com.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

/**
 * Requires a shared API key header on every request except health/info and API docs.
 * If no key is configured the filter is disabled (local dev); with
 * pricing.security.require-api-key=true (prod profile) startup fails instead.
 */
@Slf4j
@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    private static final List<String> OPEN_PREFIXES = List.of(
            "/actuator/health", "/actuator/info", "/v3/api-docs", "/swagger-ui");

    private final byte[] apiKey;
    private final String headerName;

    public ApiKeyFilter(@Value("${pricing.security.api-key:}") String apiKey,
                        @Value("${pricing.security.header-name:X-API-Key}") String headerName,
                        @Value("${pricing.security.require-api-key:false}") boolean required) {
        if (apiKey == null || apiKey.isBlank()) {
            if (required) {
                throw new IllegalStateException("PRICING_API_KEY must be set (pricing.security.require-api-key=true)");
            }
            log.warn("PRICING_API_KEY not set - API key check is DISABLED (dev only)");
            this.apiKey = null;
        } else {
            this.apiKey = apiKey.getBytes(StandardCharsets.UTF_8);
        }
        this.headerName = headerName;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (apiKey == null) {
            return true;
        }
        String path = request.getRequestURI().substring(request.getContextPath().length());
        return OPEN_PREFIXES.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String provided = request.getHeader(headerName);
        if (provided != null && MessageDigest.isEqual(apiKey, provided.getBytes(StandardCharsets.UTF_8))) {
            chain.doFilter(request, response);
            return;
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"error\":\"missing or invalid API key\"}");
    }
}
