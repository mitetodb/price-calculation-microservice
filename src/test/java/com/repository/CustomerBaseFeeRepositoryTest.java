package com.repository;

import com.model.CustomerBaseFee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CustomerBaseFeeRepositoryTest {

    @Autowired
    private CustomerBaseFeeRepository repository;

    @Test
    void testSaveAndFind() {
        UUID customerId = UUID.randomUUID();
        CustomerBaseFee fee = new CustomerBaseFee(
                customerId,
                10.50,
                LocalDateTime.now()
        );

        repository.save(fee);

        Optional<CustomerBaseFee> found = repository.findByCustomerId(customerId);

        assertThat(found).isPresent();
        assertThat(found.get().getBaseServiceFee()).isEqualTo(10.50);
        assertThat(found.get().getCustomerId()).isEqualTo(customerId);
    }
    
    @Test
    void testExistsByCustomerId() {
        UUID customerId = UUID.randomUUID();
        CustomerBaseFee fee = new CustomerBaseFee(customerId, 20.0, LocalDateTime.now());
        repository.save(fee);
        
        boolean exists = repository.existsByCustomerId(customerId);
        assertThat(exists).isTrue();
    }

    @Test
    void testExistsByCustomerId_ReturnsFalse_WhenFeeDoesNotExist() {
        UUID customerId = UUID.randomUUID();
        
        boolean exists = repository.existsByCustomerId(customerId);
        assertThat(exists).isFalse();
    }

    @Test
    void testDeleteByCustomerId() {
        UUID customerId = UUID.randomUUID();
        CustomerBaseFee fee = new CustomerBaseFee(customerId, 30.0, LocalDateTime.now());
        repository.save(fee);
        
        repository.deleteByCustomerId(customerId);
        
        Optional<CustomerBaseFee> found = repository.findByCustomerId(customerId);
        assertThat(found).isEmpty();
    }

    @Test
    void testDeleteByCustomerId_DoesNotThrowException_WhenFeeDoesNotExist() {
        UUID customerId = UUID.randomUUID();
        
        // Should not throw exception
        repository.deleteByCustomerId(customerId);
        
        Optional<CustomerBaseFee> found = repository.findByCustomerId(customerId);
        assertThat(found).isEmpty();
    }

    @Test
    void testDeleteByCustomerId_OnlyDeletesSpecifiedCustomer() {
        UUID customerId1 = UUID.randomUUID();
        UUID customerId2 = UUID.randomUUID();
        CustomerBaseFee fee1 = new CustomerBaseFee(customerId1, 40.0, LocalDateTime.now());
        CustomerBaseFee fee2 = new CustomerBaseFee(customerId2, 50.0, LocalDateTime.now());
        repository.save(fee1);
        repository.save(fee2);
        
        repository.deleteByCustomerId(customerId1);
        
        assertThat(repository.findByCustomerId(customerId1)).isEmpty();
        assertThat(repository.findByCustomerId(customerId2)).isPresent();
        assertThat(repository.findByCustomerId(customerId2).get().getBaseServiceFee()).isEqualTo(50.0);
    }
}
