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
}
