package com.bilisimio.dto;

import com.bilisimio.enums.AddressType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddressInput(@NotNull AddressType addressType, @NotBlank String addressField) {
}
