package com.bilisimio.controller;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;

import com.bilisimio.dto.BlogPage;
import com.bilisimio.dto.CreateBlogInput;
import com.bilisimio.dto.UpdateBlogInput;
import com.bilisimio.entity.Blog;
import com.bilisimio.entity.Blogger;
import com.bilisimio.entity.Category;
import com.bilisimio.service.BlogService;
import com.bilisimio.service.BloggerService;
import com.bilisimio.service.CategoryService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@Controller
@RequiredArgsConstructor
public class BlogController {

	private final BlogService blogService;
	private final BloggerService bloggerService;
	private final CategoryService categoryService;

	@QueryMapping
	public Blog blog(@Argument Long id) {
		return blogService.view(id).orElse(null);
	}

	@QueryMapping
	public BlogPage blogs(@Argument @Min(0) int page, @Argument @Min(1) @Max(100) int size) {
		return blogService.findPage(page, size);
	}

	@QueryMapping
	public List<Blog> searchBlogs(@Argument String keyword) {
		return blogService.search(keyword);
	}

	@QueryMapping
	public List<Blog> blogsByCategory(@Argument Long categoryId) {
		return blogService.findByCategory(categoryId);
	}

	@QueryMapping
	public Blog getBlogById(@Argument Long id) {
		return blog(id);
	}

	@QueryMapping
	public List<Blog> getAllBlogs() {
		return blogService.findAll();
	}

	@MutationMapping
	public Blog createBlog(@Argument @Valid CreateBlogInput input) {
		return blogService.create(input);
	}

	@MutationMapping
	public Blog updateBlog(@Argument Long id, @Argument @Valid UpdateBlogInput input) {
		return blogService.update(id, input);
	}

	@MutationMapping
	public boolean deleteBlog(@Argument Long id) {
		return blogService.delete(id);
	}

	@SubscriptionMapping
	public Flux<Blog> blogPublished() {
		return blogService.published();
	}

	/** Bir sorgudaki tüm blogların yazarları tek seferde yüklenir. */
	@BatchMapping
	public Map<Blog, Blogger> author(List<Blog> blogs) {
		Map<Long, Blogger> authors = bloggerService.findAllByIds(blogs.stream().map(Blog::getAuthorId).toList());
		return blogs.stream().distinct().filter(b -> authors.containsKey(b.getAuthorId()))
				.collect(Collectors.toMap(Function.identity(), b -> authors.get(b.getAuthorId())));
	}

	@BatchMapping
	public Map<Blog, List<Category>> categoryList(List<Blog> blogs) {
		Map<Long, Category> categories = categoryService.findAllByIds(
				blogs.stream().flatMap(b -> b.getCategoryIds().stream()).toList());
		return blogs.stream().distinct().collect(Collectors.toMap(Function.identity(),
				b -> b.getCategoryIds().stream().map(categories::get).filter(c -> c != null).toList()));
	}

}
