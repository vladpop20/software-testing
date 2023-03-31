package com.amigoscode.testing.payment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class PaymentRequest {

    private final Payment payment;

}
