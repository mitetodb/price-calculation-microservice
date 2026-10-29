package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@org.springframework.boot.context.properties.EnableConfigurationProperties(com.config.PricingProperties.class)
public class PriceCalculationMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PriceCalculationMicroserviceApplication.class, args);
	}

}
