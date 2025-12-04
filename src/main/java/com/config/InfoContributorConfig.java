package com.config;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

@Component
public class InfoContributorConfig implements InfoContributor {

    @Override
    public void contribute(Info.Builder builder) {

        builder
                .withDetail("service", "Price Calculation Microservice")
                .withDetail("version", "1.0.0")
                .withDetail("description", "Provides MS Fee + Client Service Fee + Currency Conversion")
                .withDetail("developer", "Dimi Tsvetkov");
    }
}
