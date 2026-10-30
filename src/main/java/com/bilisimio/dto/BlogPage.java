package com.bilisimio.dto;

import java.util.List;

import com.bilisimio.entity.Blog;

public record BlogPage(List<Blog> content, int page, int size, int totalElements, int totalPages, boolean hasNext) {

	public static BlogPage of(List<Blog> all, int page, int size) {
		int total = all.size();
		int from = Math.min(page * size, total);
		int to = Math.min(from + size, total);
		int totalPages = (total + size - 1) / size;
		return new BlogPage(List.copyOf(all.subList(from, to)), page, size, total, totalPages, to < total);
	}

}
