package com.amigoscode.testing.customer;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerRegistrationService {

    private final CustomerRepository customerRepository;

    public CustomerRegistrationService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void registerCustomer(CustomerRegistrationRequest request) {
        String phoneNumber = request.getCustomer().getPhoneNumber();
        Optional<Customer> existingCustomerOptional = customerRepository.selectCustomerByPhoneNumber(phoneNumber);

        if (existingCustomerOptional.isPresent()) {
            Customer customer = existingCustomerOptional.get();
            if (customer.getName().equalsIgnoreCase(request.getCustomer().getName())) {
                return;
            }
            throw new IllegalStateException(String.format("A customer with this phone number %s, already exists", phoneNumber));
        }

        customerRepository.save(request.getCustomer());
    }
}
