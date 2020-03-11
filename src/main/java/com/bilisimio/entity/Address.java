package com.bilisimio.entity;

import com.bilisimio.enums.AddressType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

	private Long id;
	private AddressType addressType;
	private String addressField;

}
