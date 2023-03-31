package com.amigoscode.testing.payment;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
public class CardPaymentCharge {

    private final boolean isCardDebited;

    public boolean isCardDebited() {
        return isCardDebited;
    }
}
