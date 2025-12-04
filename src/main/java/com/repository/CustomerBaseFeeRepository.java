package com.repository;

import com.model.CustomerBaseFee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerBaseFeeRepository extends JpaRepository<CustomerBaseFee, Long> {

    Optional<CustomerBaseFee> findByCustomerId(UUID customerId);

    void deleteByCustomerId(UUID customerId);

    boolean existsByCustomerId(UUID customerId);
}
