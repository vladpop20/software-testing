package com.amigoscode.testing.payment;

import com.amigoscode.testing.customer.Customer;
import com.amigoscode.testing.customer.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.*;

//@SpringBootTest
class PaymentServiceTest {

    @Mock
    private  CustomerRepository customerRepository;

    @Mock
    private  PaymentRepository paymentRepository;

    @Mock
    private  CardPaymentCharger cardPaymentCharger;

    @Mock
    private Customer customer;

    private PaymentService underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new PaymentService(customerRepository, paymentRepository, cardPaymentCharger);
    }

    @Test
    void itShouldChargeCardSuccessfully() {
        // Given
        UUID customerId = UUID.randomUUID();

        // ... Customer exists
        given(customerRepository.findById(customerId)).willReturn(Optional.of(customer));       // or I could use: (Optional.of(mock(Customer.class))

        // ... Payment request
        PaymentRequest paymentRequest = new PaymentRequest(
                new Payment(
                        null,
                        null,
                        new BigDecimal("110.0"),
                        Currency.EUR,
                        "card2598xxx",
                        "Bought a 2.1 audio system"
                )
        );

        // ... Card is charged successfully
        given(cardPaymentCharger.chargeCard(
                paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getAmount(),
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getDescription()
        )).willReturn(new CardPaymentCharge(true));


        // When
        underTest.chargeCard(customerId, paymentRequest);

        // Then
        ArgumentCaptor<Payment> paymentArgumentCaptor = ArgumentCaptor.forClass(Payment.class);

        then(paymentRepository).should().save(paymentArgumentCaptor.capture());

        Payment paymentArgumentCaptorValue = paymentArgumentCaptor.getValue();
        assertThat(paymentArgumentCaptorValue).usingRecursiveComparison()
                .ignoringFields("customerId")
                .isEqualTo(paymentRequest.getPayment());

        assertThat(paymentArgumentCaptorValue.getCustomerId()).isEqualByComparingTo(customerId);
    }

    @Test
    void itShouldThrowWhenCardIsNotCharged() {
        // Given
        UUID customerId = UUID.randomUUID();

        // ... Customer exists
        given(customerRepository.findById(customerId)).willReturn(Optional.of(customer));       // or I could use: (Optional.of(mock(Customer.class))

        // ... Payment request
        PaymentRequest paymentRequest = new PaymentRequest(
                new Payment(
                        null,
                        null,
                        new BigDecimal("110.0"),
                        Currency.EUR,
                        "card2598xxx",
                        "Bought a 2.1 audio system"
                )
        );

        // ... Card is not charged successfully
        given(cardPaymentCharger.chargeCard(
                paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getAmount(),
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getDescription()
        )).willReturn(new CardPaymentCharge(false));


        // When
        assertThatThrownBy(() -> underTest.chargeCard(customerId, paymentRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Card was not debited for customer " + customerId);

        // Then
        then(paymentRepository).should(never()).save(any(Payment.class));
    }

    @Test
    void itShouldNotChargeCardAndThrowWhenCurrencyNotSupported() {
        // Given
        UUID customerId = UUID.randomUUID();

        // ... Customer exists
        given(customerRepository.findById(customerId)).willReturn(Optional.of(customer));       // or I could use: (Optional.of(mock(Customer.class))

        // ... Payment request
        PaymentRequest paymentRequest = new PaymentRequest(
                new Payment(
                        null,
                        null,
                        new BigDecimal("110.0"),
                        Currency.GBP,
                        "card2598xxx",
                        "Bought a 2.1 audio system"
                )
        );

        // When
        assertThatThrownBy(() -> underTest.chargeCard(customerId, paymentRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("Currency %s is not supported", paymentRequest.getPayment().getCurrency()));

        // Then
        // ... No interactions with cardPaymentCharger
        then(cardPaymentCharger).shouldHaveNoInteractions();

        // ... No interactions with paymentRepository
        then(paymentRepository).shouldHaveNoInteractions();
    }

    @Test
    void itShouldNotChargeAndThrowWhenCustomerNotFound() {
        // Given
        UUID customerId = UUID.randomUUID();

        // Customer nor found
        given(customerRepository.findById(customerId)).willReturn(Optional.empty());

        // When
        // Then
        assertThatThrownBy(() -> underTest.chargeCard(customerId, new PaymentRequest(new Payment())))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("Customer with Id: %s not found!", customerId));

        // ... No interactions with PaymentCharger nor Payment Repository
        then(cardPaymentCharger).shouldHaveNoInteractions();
        then(paymentRepository).shouldHaveNoInteractions();
    }
}