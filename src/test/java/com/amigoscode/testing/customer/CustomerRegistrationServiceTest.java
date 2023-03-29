package com.amigoscode.testing.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;


import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CustomerRegistrationServiceTest {

    @Mock
    private CustomerRepository customerRepository;
//    private CustomerRepository customerRepository = mock(CustomerRepository.class);   // both ways are fine

    @Captor
    private ArgumentCaptor<Customer> customerArgumentCaptor;

    private CustomerRegistrationService underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new CustomerRegistrationService(customerRepository);
    }

    @Test
    void itShouldSaveNewCustomer() {
        // Given phone number and a customer
        String phoneNumber = "08900";
        Customer customer = new Customer(UUID.randomUUID(), "Bianca", phoneNumber);

        // ... and a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        // ... no customer exists with the phone number passed
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber))
                .willReturn(Optional.empty());

        // When
        underTest.registerCustomer(request);

        // Then
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();
        assertThat(customerArgumentCaptorValue).usingRecursiveComparison()
                .isEqualTo(customer);
    }

    @Test
    void itShouldSaveNewCustomerWhenIdIsNull() {
        // Given phone number and a customer
        String phoneNumber = "08900";
        Customer customer = new Customer(null, "Tony", phoneNumber);

        // ... and a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        // ... no customer exists with the phone number passed
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber))
                .willReturn(Optional.empty());

        // When
        underTest.registerCustomer(request);

        // Then
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();

        assertThat(customerArgumentCaptorValue).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(customer);
        assertThat(customerArgumentCaptorValue.getId()).isNotNull();
    }

    @Test
    void itShouldNotSaveCustomerWhenCustomerExists() {
        // Given phone number and a customer
        String phoneNumber = "08900";
        Customer customer = new Customer(UUID.randomUUID(), "Tudor", phoneNumber);

        // ... and a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        // ... a customer with phone number passed exists
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber))
                .willReturn(Optional.of(customer));

        // When
        underTest.registerCustomer(request);

        // Then
        then(customerRepository).should(never()).save(any(Customer.class));
    }

    @Test
    void itShouldThrowWhenPhoneNumberIsTaken() {
        // Given phone number and a customer
        String phoneNumber = "089010";
        Customer customer = new Customer(UUID.randomUUID(), "Tudor", phoneNumber);
        Customer customerTwo = new Customer(UUID.randomUUID(), "Maria", phoneNumber);

        // ... and a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        // ... a customer with phone number passed exists
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber))
                .willReturn(Optional.of(customerTwo));

        // When


        // Then
        assertThatThrownBy(() -> underTest.registerCustomer(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("A customer with this phone number %s, already exists", phoneNumber));

        // Then
        then(customerRepository).should(never()).save(any(Customer.class));
    }
}