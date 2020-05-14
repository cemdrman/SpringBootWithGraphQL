package com.bilisimio.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bilisimio.entity.Blog;
import com.bilisimio.entity.Category;
import com.bilisimio.service.BlogService;

@Service
public class BlogServiceImpl implements BlogService {

	private static List<Blog> blogList = new ArrayList<>();

	static {

		List<Category> categories = new ArrayList<>();
		categories.add(new Category(1L, "GraphQL"));
		categories.add(new Category(1L, "Java"));
		categories.add(new Category(1L, "Spring Boot"));

		blogList.add(new Blog(1L, "Spring Boot Ile GraphQL API – 1", "Yeni Nesil API",
				"https://bilisim.io/2020/02/17/spring-boot-ile-graphql-api-1/", categories));
		blogList.add(new Blog(2L, "Spring Boot Ile GraphQL API – 1", "Yeni Nesil API",
				"https://bilisim.io/2020/02/17/spring-boot-ile-graphql-api-2/", categories));
		blogList.add(new Blog(3L, "Spring Boot Ile GraphQL API – 1", "Yeni Nesil API",
				"https://bilisim.io/2020/02/17/spring-boot-ile-graphql-api-3/", categories));
		blogList.add(new Blog(4L, "Spring Boot Ile GraphQL API – 1", "Yeni Nesil API",
				"https://bilisim.io/2020/02/17/spring-boot-ile-graphql-api-4/", categories));
		blogList.add(new Blog(5L, "Spring Boot Ile GraphQL API – 1", "Yeni Nesil API",
				"https://bilisim.io/2020/02/17/spring-boot-ile-graphql-api-5/", categories));

	}

	public Blog getBlogById(Long id) {
		return blogList.stream().filter(blog -> blog.getId().equals(id)).findAny().orElse(null);
	}

	public List<Blog> getAllBlogs() {
		return blogList;
	}

}
