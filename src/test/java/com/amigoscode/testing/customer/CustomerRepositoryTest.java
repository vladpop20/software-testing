package com.amigoscode.testing.customer;

//import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository underTest;

    @Test
    void itShouldSelectCustomerByPhoneNumber() {
        // Given
        String phoneNumber = "07070";
        Customer customer = new Customer(UUID.randomUUID(), "Marcel", phoneNumber);

        // When
        underTest.save(customer);

        // Then
        Optional<Customer> optionalCustomer = underTest.selectCustomerByPhoneNumber(phoneNumber);
        assertThat(optionalCustomer)
                .isPresent()
                .hasValueSatisfying(c -> {
                    assertThat(c)
                            .usingRecursiveComparison()
                            .isEqualTo(customer);
                });
    }

    @Test
    public void itShouldSaveCustomer() {
        // Given
        UUID id = UUID.randomUUID();
        Customer customerAdrian = new Customer(id, "Adrian", "0778901234");

        // When
        underTest.save(customerAdrian);

        // Then
        Optional<Customer> optionalCustomer = underTest.findById(id);
        assertThat(optionalCustomer)
                .isPresent()
                .hasValueSatisfying(c -> {
//                    assertThat(c.getId()).isEqualByComparingTo(id);
//                    assertThat(c.getName()).isEqualTo("Adrian");
//                    assertThat(c.getPhoneNumber()).isEqualTo("0778901234");
                    assertThat(c).usingRecursiveComparison().isEqualTo(customerAdrian);
                });
    }
}