package com.bilisimio.dto;

import com.bilisimio.enums.GenderType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateBloggerInput(
		@NotBlank String name,
		@NotBlank String surname,
		@NotBlank @Email String email,
		@NotBlank @Size(min = 6, message = "en az 6 karakter olmalıdır") String password,
		GenderType gender,
		@Valid AddressInput address) {
}
