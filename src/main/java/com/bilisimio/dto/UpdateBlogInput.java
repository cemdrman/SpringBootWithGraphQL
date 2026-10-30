package com.bilisimio.dto;

import java.util.List;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * {@code null} alanlar güncellenmez.
 */
public record UpdateBlogInput(
		@Pattern(regexp = ".*\\S.*", message = "boş olamaz") @Size(max = 200) String title,
		@Pattern(regexp = ".*\\S.*", message = "boş olamaz") String content,
		String imageUrl,
		List<Long> categoryIds) {
}
