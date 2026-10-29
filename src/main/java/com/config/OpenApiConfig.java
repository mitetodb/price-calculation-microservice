package com.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI pricingOpenApi(@Value("${pricing.security.header-name:X-API-Key}") String header) {
        return new OpenAPI()
                .info(new Info()
                        .title("Price Calculation Microservice")
                        .version("1.0.0")
                        .description("Internal pricing API for test-purchase-application. "
                                + "Not publicly exposed; requires the " + header + " header."))
                .components(new Components().addSecuritySchemes("apiKey",
                        new SecurityScheme().type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER).name(header)))
                .addSecurityItem(new SecurityRequirement().addList("apiKey"));
    }
}
