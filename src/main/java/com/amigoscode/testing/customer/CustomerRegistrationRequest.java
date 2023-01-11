package com.amigoscode.testing.customer;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CustomerRegistrationRequest {

	private final Customer customer;

	public CustomerRegistrationRequest(@JsonProperty Customer customer) {
		this.customer = customer;
	}
}
