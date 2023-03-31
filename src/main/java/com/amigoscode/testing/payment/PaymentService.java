package com.amigoscode.testing.payment;

import com.amigoscode.testing.customer.Customer;
import com.amigoscode.testing.customer.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final CustomerRepository customerRepository;

    private final PaymentRepository paymentRepository;

    private final CardPaymentCharger cardPaymentCharger;

    private static final List<Currency> ACCEPTED_CURRENCIES = List.of(Currency.EUR, Currency.USD);


    void chargeCard(UUID customerId, PaymentRequest paymentRequest) {
        // Does customer exist if not throw
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        if (optionalCustomer.isEmpty()) {
            throw new IllegalStateException(String.format("Customer with Id: %s not found!", customerId));
        }

        // Do we support that currency if not throw
        boolean isCurrencyAccepted = ACCEPTED_CURRENCIES.contains(paymentRequest.getPayment().getCurrency());

        if (!isCurrencyAccepted) {
            String message = String.format("Currency %s is not supported", paymentRequest.getPayment().getCurrency());
            throw new IllegalStateException(message);
        }

        // Charge card
        CardPaymentCharge cardPaymentCharge = cardPaymentCharger.chargeCard(
                paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getAmount(),
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getDescription()
        );

        // If not debited throw
        if (!cardPaymentCharge.isCardDebited()) {
            throw new IllegalStateException(String.format("Card was not debited for customer %s", customerId));
        }

        // Insert payment
        paymentRequest.getPayment().setCustomerId(customerId);

        paymentRepository.save(paymentRequest.getPayment());

        // TODO: send SMS
    }
}
