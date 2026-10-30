package com.bilisimio.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Blog {

	private Long id;
	private String title;
	private String content;
	private String imageUrl;
	private Long authorId;
	private List<Long> categoryIds = new ArrayList<>();
	private Instant publishedAt;

}
