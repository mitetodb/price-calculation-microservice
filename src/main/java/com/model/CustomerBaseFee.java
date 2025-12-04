package com.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "customer_base_fees",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_customer_base_fees_customer_id", columnNames = "customer_id")
       })
public class CustomerBaseFee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false, updatable = false)
    private UUID customerId;

    @Column(name = "base_service_fee", nullable = false)
    private Double baseServiceFee;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    public CustomerBaseFee() {
    }

    public CustomerBaseFee(UUID customerId, Double baseServiceFee, LocalDateTime changedAt) {
        this.customerId = customerId;
        this.baseServiceFee = baseServiceFee;
        this.changedAt = changedAt;
    }

    public Long getId() {
        return id;
    }

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
