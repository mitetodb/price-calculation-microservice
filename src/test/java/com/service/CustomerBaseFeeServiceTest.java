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

    @Test
    void testCreateBaseFee_WhenFeeAlreadyExists_ShouldUpdateInstead() {
        UUID customerId = UUID.randomUUID();
        CustomerBaseFee existing = new CustomerBaseFee(customerId, 10.0, LocalDateTime.now());
        repository.save(existing);

        CustomerBaseFeeDTO dto = new CustomerBaseFeeDTO();
        dto.setCustomerId(customerId);
        dto.setBaseServiceFee(75.0);
        dto.setChangedAt(LocalDateTime.now());

        service.createBaseFee(customerId, dto);

        CustomerBaseFee updated = repository.findByCustomerId(customerId).orElseThrow();
        assertThat(updated.getBaseServiceFee()).isEqualTo(75.0);
    }

    @Test
    void testDeleteBaseFee() {
        UUID customerId = UUID.randomUUID();
        CustomerBaseFee existing = new CustomerBaseFee(customerId, 10.0, LocalDateTime.now());
        repository.save(existing);

        service.deleteBaseFee(customerId);

        assertThat(repository.findByCustomerId(customerId)).isEmpty();
    }

    @Test
    void testDeleteBaseFee_WhenFeeDoesNotExist_ShouldNotThrowException() {
        UUID customerId = UUID.randomUUID();

        // Should not throw exception
        service.deleteBaseFee(customerId);

        assertThat(repository.findByCustomerId(customerId)).isEmpty();
    }

    @Test
    void testFindByCustomerId_WhenFeeExists() {
        UUID customerId = UUID.randomUUID();
        CustomerBaseFee existing = new CustomerBaseFee(customerId, 100.0, LocalDateTime.now());
        repository.save(existing);

        var result = service.findByCustomerId(customerId);

        assertThat(result).isPresent();
        assertThat(result.get().getBaseServiceFee()).isEqualTo(100.0);
        assertThat(result.get().getCustomerId()).isEqualTo(customerId);
    }

    @Test
    void testFindByCustomerId_WhenFeeDoesNotExist() {
        UUID customerId = UUID.randomUUID();

        var result = service.findByCustomerId(customerId);

        assertThat(result).isEmpty();
    }

    @Test
    void testUpdateBaseFee_WhenFeeDoesNotExist_ShouldCreateNew() {
        UUID customerId = UUID.randomUUID();

        CustomerBaseFeeDTO updateDto = new CustomerBaseFeeDTO();
        updateDto.setBaseServiceFee(99.99);
        updateDto.setChangedAt(LocalDateTime.now());

        service.updateBaseFee(customerId, updateDto);

        CustomerBaseFee created = repository.findByCustomerId(customerId).orElseThrow();
        assertThat(created.getBaseServiceFee()).isEqualTo(99.99);
        assertThat(created.getCustomerId()).isEqualTo(customerId);
    }
}
