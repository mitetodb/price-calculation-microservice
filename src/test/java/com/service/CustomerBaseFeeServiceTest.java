package com.service;

import com.model.CustomerBaseFee;
import com.model.dto.CustomerBaseFeeDTO;
import com.repository.CustomerBaseFeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class CustomerBaseFeeServiceTest {

    @Autowired
    private CustomerBaseFeeService service;

    @Autowired
    private CustomerBaseFeeRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void testCreateBaseFee() {
        UUID customerId = UUID.randomUUID();
        
        CustomerBaseFeeDTO dto = new CustomerBaseFeeDTO();
        dto.setCustomerId(customerId);
        dto.setBaseServiceFee(25.00);
        dto.setChangedAt(LocalDateTime.now());

        service.createBaseFee(customerId, dto);

        Optional<CustomerBaseFee> saved = repository.findByCustomerId(customerId);
        assertThat(saved).isPresent();
        assertThat(saved.get().getBaseServiceFee()).isEqualTo(25.00);
    }

    @Test
    void testUpdateBaseFee() {
        UUID customerId = UUID.randomUUID();
        CustomerBaseFee existing = new CustomerBaseFee(customerId, 10.0, LocalDateTime.now());
        repository.save(existing);

        CustomerBaseFeeDTO updateDto = new CustomerBaseFeeDTO();
        updateDto.setBaseServiceFee(50.0);
        updateDto.setChangedAt(LocalDateTime.now());

        service.updateBaseFee(customerId, updateDto);

        CustomerBaseFee updated = repository.findByCustomerId(customerId).orElseThrow();
        assertThat(updated.getBaseServiceFee()).isEqualTo(50.0);
    }
}
