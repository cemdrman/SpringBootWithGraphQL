package com.bilisimio.messaging;

import java.time.Instant;

import com.bilisimio.entity.Blog;

public record BlogEvent(BlogEventType type, Blog blog, Instant occurredAt) {

	public static BlogEvent of(BlogEventType type, Blog blog) {
		return new BlogEvent(type, blog, Instant.now());
	}

}
