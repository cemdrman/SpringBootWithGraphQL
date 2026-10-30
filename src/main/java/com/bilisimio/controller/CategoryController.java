package com.bilisimio.controller;

import java.util.List;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import com.bilisimio.entity.Blog;
import com.bilisimio.entity.Category;
import com.bilisimio.service.BlogService;
import com.bilisimio.service.CategoryService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CategoryController {

	private final CategoryService categoryService;
	private final BlogService blogService;

	@QueryMapping
	public List<Category> categories() {
		return categoryService.findAll();
	}

	@QueryMapping
	public Category category(@Argument Long id) {
		return categoryService.findById(id).orElse(null);
	}

	@MutationMapping
	public Category createCategory(@Argument String name) {
		return categoryService.create(name);
	}

	@SchemaMapping
	public List<Blog> blogs(Category category) {
		return blogService.findByCategory(category.getId());
	}

}
