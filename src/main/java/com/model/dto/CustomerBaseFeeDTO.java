package com.model.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class CustomerBaseFeeDTO {

    private UUID customerId;
    private Double baseServiceFee;
    private LocalDateTime changedAt;

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public Double getBaseServiceFee() {
        return baseServiceFee;
    }

    public void setBaseServiceFee(Double baseServiceFee) {
        this.baseServiceFee = baseServiceFee;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }
}
