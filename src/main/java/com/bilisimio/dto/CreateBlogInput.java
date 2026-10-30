package com.bilisimio.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateBlogInput(
		@NotBlank @Size(max = 200) String title,
		@NotBlank String content,
		String imageUrl,
		@NotNull Long authorId,
		List<Long> categoryIds) {
}
