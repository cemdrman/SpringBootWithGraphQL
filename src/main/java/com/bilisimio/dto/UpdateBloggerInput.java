package com.bilisimio.dto;

import com.bilisimio.enums.GenderType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

/**
 * updateBlogger mutation'ının düz argümanları. {@code null} alanlar güncellenmez.
 */
public record UpdateBloggerInput(
		Long id,
		@Email String email,
		@Pattern(regexp = ".*\\S.*", message = "boş olamaz") String name,
		@Pattern(regexp = ".*\\S.*", message = "boş olamaz") String surname,
		GenderType gender,
		@Valid AddressInput address) {
}
