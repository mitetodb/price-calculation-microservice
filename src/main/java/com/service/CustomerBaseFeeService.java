package com.service;

import com.model.CustomerBaseFee;
import com.model.dto.CustomerBaseFeeDTO;
import com.repository.CustomerBaseFeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerBaseFeeService {

    private final CustomerBaseFeeRepository repository;

    public CustomerBaseFeeService(CustomerBaseFeeRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void createBaseFee(UUID customerId, CustomerBaseFeeDTO dto) {
        if (repository.existsByCustomerId(customerId)) {
            updateBaseFee(customerId, dto);
            return;
        }

        CustomerBaseFee entity = new CustomerBaseFee();
        entity.setCustomerId(customerId);
        entity.setBaseServiceFee(dto.getBaseServiceFee());
        entity.setChangedAt(LocalDateTime.now());

        repository.save(entity);
    }

    @Transactional
    public void updateBaseFee(UUID customerId, CustomerBaseFeeDTO dto) {
        CustomerBaseFee entity = repository.findByCustomerId(customerId)
                .orElseGet(() -> {
                    CustomerBaseFee newEntity = new CustomerBaseFee();
                    newEntity.setCustomerId(customerId);
                    return newEntity;
                });

        entity.setBaseServiceFee(dto.getBaseServiceFee());
        entity.setChangedAt(LocalDateTime.now());

        repository.save(entity);
    }

    @Transactional
    public void deleteBaseFee(UUID customerId) {
        repository.deleteByCustomerId(customerId);
    }

    @Transactional(readOnly = true)
    public Optional<CustomerBaseFee> findByCustomerId(UUID customerId) {
        return repository.findByCustomerId(customerId);
    }
}
