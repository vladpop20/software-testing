package com.amigoscode.testing.customer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Customer {

	@Id
	private UUID id;

	@NotBlank
	@Column(nullable = false)
	private String name;

	@NotBlank
	@Column(nullable = false, unique = true)
	private String phoneNumber;

	public Customer(UUID id, String name, String phoneNumber) {
		this.id = id;
		this.name = name;
		this.phoneNumber = phoneNumber;
	}


}
